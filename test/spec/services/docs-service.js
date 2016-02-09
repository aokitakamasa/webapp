'use strict';

describe('Service: docsService', function () {

  // load the service's module
  beforeEach(module('panelApp'));

  // instantiate service
  var docsService;
  beforeEach(inject(function (_docsService_) {
    docsService = _docsService_;
  }));

  it('should do something', function () {
    expect(!!docsService).toBe(true);
  });

});
