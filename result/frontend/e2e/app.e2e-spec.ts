import { Frontend2Page } from './app.po';

describe('frontend2 App', function() {
  let page: Frontend2Page;

  beforeEach(() => {
    page = new Frontend2Page();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
