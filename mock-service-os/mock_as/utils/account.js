import { MongoClient } from 'mongodb'; // eslint-disable-line import/no-unresolved
import Debug from 'debug';
import Credential from "./credential.js";
const log = Debug('raidiam:server:info');

let DB;

class Account {
  constructor(result) {
    this._id = result._id;
    this.sub =  result.sub;
    this.accountId =  result.sub;
    this.given_name =  result.given_name;
    this.family_name =  result.family_name;
    this.national_id_signing_device_present =  result.national_id_signing_device_present;
    this.national_id_verified =  result.national_id_verified;
    this.email =  result.email;
    this.email_verified =  result.email_verified;
    this.birthdate =  result.birthdate;
    this.createdAt =  result.createdAt;
    this.updatedAt = result.updatedAt;
  }

  async claims(use, scope) { // eslint-disable-line no-unused-vars
    if (this.profile) {
      return {
        sub: this.accountId, // it is essential to always return a sub claim
        email: this.profile.email,
        email_verified: this.profile.email_verified,
        family_name: this.profile.family_name,
        given_name: this.profile.given_name,
        locale: this.profile.locale,
        name: this.profile.name,
      };
    }

    return {
      sub: this.accountId, // it is essential to always return a sub claim

      address: {
        country: '000',
        formatted: '000',
        locality: '000',
        postal_code: '000',
        region: '000',
        street_address: '000',
      },
      birthdate: this.birthdate,
      email: this.email,
      email_verified: this.email_verified,
      family_name: this.family_name,
      gender: 'male',
      given_name: this.given_name,
      locale: 'en-US',
      middle_name: 'Middle',
      name: 'John Doe',
      nickname: 'Johny',
      phone_number: '+49 000 000000',
      phone_number_verified: false,
      picture: 'http://lorempixel.com/400/200/',
      preferred_username: 'johnny',
      profile: 'https://johnswebsite.com',
      updated_at: this.updatedAt,
      website: 'http://example.com',
      zoneinfo: 'Europe/Berlin',
    };
  }

  static async initialiseAdapter(collection) {
    log(`Initializing mongo Account connection`);
    const connection = await MongoClient.connect(process.env.MONGODB_URI);
    DB = connection.db(collection);
  }

  static coll() {
    return DB.collection("accounts");
  }

  static async findByLogin(login) {
    const result = await Account.coll().find(
      { 'sub': login },
    ).limit(1).next();

    if (!result) {
      log("account not found in collection")
      return undefined;
    }

    return new Account(result);
  }

  static async authenticate(login, password) {
    const account = await this.findByLogin(login);
    const validCredential = await Credential.validatePassword(DB, account._id, password);
    console.log(validCredential);
    if (!validCredential) {
      return undefined;
    }

    return account;
  }

  static async findAccount(ctx, id, token) { // eslint-disable-line no-unused-vars
    // token is a reference to the token used for which a given account is being loaded,
    //   it is undefined in scenarios where account claims are returned from authorization endpoint
    // ctx is the koa request context
    const result = await Account.coll().find(
      { 'sub': id },
    ).limit(1).next();
    if (!result) {
      log("account not found in collection")
      return undefined;
    }

    return new Account(result);
  }
}

export default Account;
