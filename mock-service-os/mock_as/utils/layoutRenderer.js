// Wraps res.render so every login/consent/CIBA/error page renders inside
// _layout.ejs, and scopes form-action 'self' to just those pages (INCM-149).
const REDIRECTING_VIEWS = ['interaction', 'ciba'];

export function installLayoutRenderer(app, layout) {
  app.use((req, res, next) => {
    const orig = res.render;
    res.render = (view, locals) => {
      app.render(view, locals, (err, html) => {
        if (err) throw err;
        // Skip consent/enrollment and CIBA views: confirm/abort there must be free to
        // redirect onward, including to the client's external redirect_uri (INCM-149).
        if (!REDIRECTING_VIEWS.includes(view)) {
          res.append('Content-Security-Policy', "form-action 'self'");
        }
        orig.call(res, '_layout', {
          ...locals,
          layout,
          body: html,
        });
      });
    };
    next();
  });
}
