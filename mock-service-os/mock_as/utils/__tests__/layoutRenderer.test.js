import { vi } from 'vitest';
import { installLayoutRenderer } from '../layoutRenderer.js';

function setup() {
  let middleware;
  const app = {
    use: (mw) => {
      middleware = mw;
    },
    render: vi.fn((view, locals, cb) => cb(null, `<rendered ${view}>`)),
  };
  const origRender = vi.fn();
  const res = { render: origRender, append: vi.fn() };
  const layout = { brand: 'opf' };

  installLayoutRenderer(app, layout);
  middleware({}, res, () => {});

  return { app, res, origRender, layout };
}

describe('layoutRenderer.js — installLayoutRenderer (INCM-149)', () => {
  it('restricts form-action to same-origin on the login page', () => {
    const { res } = setup();

    res.render('login', { uid: 'abc' });

    expect(res.append).toHaveBeenCalledWith('Content-Security-Policy', "form-action 'self'");
  });

  it('does not restrict form-action on the consent/enrollment page, which must redirect onward', () => {
    const { res } = setup();

    res.render('interaction', { uid: 'abc' });

    expect(res.append).not.toHaveBeenCalled();
  });

  it('does not restrict form-action on the CIBA page, which must also redirect onward', () => {
    const { res } = setup();

    res.render('ciba', { title: 'Pending authorizations' });

    expect(res.append).not.toHaveBeenCalled();
  });

  it('still wraps the rendered view in _layout with the brand layout and body', () => {
    const { res, origRender, layout } = setup();

    res.render('login', { uid: 'abc' });

    expect(origRender).toHaveBeenCalledWith('_layout', { uid: 'abc', layout, body: '<rendered login>' });
  });

  it('appends the header before writing the response, not after', () => {
    const { res, origRender } = setup();
    const callOrder = [];
    res.append.mockImplementation(() => callOrder.push('append'));
    origRender.mockImplementation(() => callOrder.push('render'));

    res.render('login', {});

    expect(callOrder).toEqual(['append', 'render']);
  });
});
