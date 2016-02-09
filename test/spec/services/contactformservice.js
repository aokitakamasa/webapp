'use strict';

describe('Service: contactFormService', function () {

  // load the service's module
  beforeEach(module('panelApp'));

  // instantiate service
  var contactFormService;
  beforeEach(inject(function (_contactFormService_) {
    contactFormService = _contactFormService_;
  }));

  it('should do something', function () {
    expect(!!contactFormService).toBe(true);
  });

});
