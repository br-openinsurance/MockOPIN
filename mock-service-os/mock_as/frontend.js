import express from 'express';
import path from 'path';
import { fileURLToPath } from "url";
import helmet from 'helmet';
import { randomBytes } from 'node:crypto';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// Serve the 'public' folder as static
app.use(express.static(path.join(__dirname, 'public')));

app.use((req, res, next) => {
    res.locals.nonce = randomBytes(16).toString("base64");
    next();
  });
const directives = helmet.contentSecurityPolicy.getDefaultDirectives();
delete directives["form-action"];
directives["script-src"] = ["'self'", (req, res) => `'nonce-${res.locals.nonce}'`];
app.use(
    helmet({
        contentSecurityPolicy: {
        useDefaults: false,
        directives,
        },
    })
);

var opfLayout = {
    'brand': 'opf',
    'rightPanelColor': '#1F3233',
    'buttonColor': '#2F3A4F',
    'buttonHoverColor': '#1D2A3B',
    'product': "Mock Bank",
    'ecosystem': 'Open Finance'
}

const layout = {
    'brand': 'opin',
    'rightPanelColor': '#1F3233',
    'buttonColor': '#0E580D',
    'buttonHoverColor': '#0A4009',
    'product': 'Mock Insurer',
    'ecosystem': 'Open Insurance'
}

app.use((req, res, next) => {

    const orig = res.render;
    res.render = (view, locals) => {
        app.render(view, locals, (err, html) => {
        if (err) throw err;
        orig.call(res, '_layout', {
            layout,
            ...locals,
            body: html,
        });
        });
    };
    next();
});

app.get("/interaction", (req, res) => {
    res.render("interaction", {
        title: "Mock Opin",
        client: { },
        locals: { },
        uid: 'interaction_id',
        params: {},
        session: {},
        dbg: {},
        details: {},
        layout
    });
});

app.get("/", (req, res) => {
    res.render("login", {
        title: "Mock Opin",
        client: { },
        locals: { },
        uid: 'interaction_id',
        params: {},
        session: {},
        dbg: {},
        layout
    });
});

app.listen(80, () => {
    console.log("Server is running on http://localhost:80");
});