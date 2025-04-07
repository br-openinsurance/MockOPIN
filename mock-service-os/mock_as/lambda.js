import dotenv from 'dotenv';
dotenv.config();
import pinoDebug from 'pino-debug';
import { lambdaRequestTracker, pinoLambdaDestination } from 'pino-lambda';
import pino from 'pino';
const destination = pinoLambdaDestination();
const levels = {
  emerg: 80,
  alert: 70,
  crit: 60,
  error: 50,
  warn: 40,
  notice: 30,
  info: 20,
  debug: 10,
  trace: 1,
};
const logger = pino.default(
  {
    level: process.env.LOG_LEVEL || 'info',
    redact: {
      paths: [
        'req.headers.Authorization',
        'req.headers.authorization',
        'req.body.password',
        'req.body["password-confirm"]',
        'ctx.request.header.authorization'
      ],
      censor: '**REDACTED**',
    },
    customLevels: levels,
    useOnlyCustomLevels: true,
    formatters: {
      level: (label) => {
        return { level: label };
      },
    },
  },
  destination,
);
pinoDebug(logger, {
  auto: true, // default
  map: {
    'raidiam:server:warn': 'warn',
    'raidiam:server:error': 'error',
    'raidiam:server:fatal': 'error',
    'raidiam:server:info': 'info',
    'raidiam:server:debug': 'debug',
    'raidiam:server:context:info': 'info',
    'raidiam:server:context:error': 'error',
    'raidiam:server:context:warn': 'warn',
    'raidiam:server:context:debug': 'debug',
    'raidiam:otp:publisher:*': 'info',
    'oidc-provider:*': 'info',
    'raidiam:*': 'debug',
    '*': 'trace', // everything else - trace
  },
});
const withRequest = lambdaRequestTracker();

import Debug from 'debug';
const log = Debug('raidiam:server:info');

import pkg from 'lodash';
const { forEach, get } = pkg;
import serverlessExpress from '@vendia/serverless-express';

import { Base64 } from 'js-base64';

let server;
let initialized;

const binaryMimeTypes = [
  'application/octet-stream',
  'font/eot',
  'font/opentype',
  'font/otf',
  'image/jpeg',
  'image/png',
  'image/svg+xml',
];

const init = async () => {
  // Read ssm params as env vars if a deployed environment
  // Initialize the server.
  // eslint-disable-next-line no-async-promise-executor
  return new Promise(async (resolve, reject) => {
    log('Stage 1: Initializing server...');
    const express = await import('./express.js');
    const app = await express.run();
    server = serverlessExpress({ app: app.app.app });
    // server = awsServerlessExpress.createServer(
    //   app,
    //   null,
    //   binaryMimeTypes,
    // );
    log('Stage 1: Server Initialized.');
    resolve('Init done');
    initialized = true;
  });
};

// With this as a top level mjs we can await the init function
const initPromise = await init();

log('Stage 2: Server start has completed. Ready to process events.');
log('Note to support staff, Stage 2 should now log complete after stage 1.');
export async function handler(event, context) {
  withRequest(event, context);
  // Ensure init has completed before proceeding
  if (!initialized) {
    await initPromise;
  }
  // If the request has come from authorize request
  // then the body will have been urlencoded which AWS will be double encoding

  if (get(event, 'requestContext.elb') && get(event, 'path') === '/.well-known/openid-configuration') {
    log(
      'Received request from ALB to the openid-configuration endpoint',
      ['event', event],
    );
  }

  if (get(event, 'requestContext.elb') && get(event, 'path') === '/auth') {
    log(
      'Request from ALB to the authorize endpoint, removing double url encoding',
      ['event', event],
    );
    forEach(event.queryStringParameters, (value, key) => {
      event.queryStringParameters[key] = decodeURIComponent(value).replace(
        '+',
        ' ',
      );
    });
  }
  if (get(event, 'requestContext.elb') && get(event, 'path') === '/token') {
    log('Parsing token request');
    event.body = Base64.decode(event.body);
    event.isBase64Encoded = false;
  }
  return server(event, context);
}