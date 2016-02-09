'use strict';

describe('Service: changePasswordService', function () {

  // load the service's module
  beforeEach(module('panelApp'));

  // instantiate service
  var changePasswordService;
  beforeEach(inject(function (_changePasswordService_) {
    changePasswordService = _changePasswordService_;
  }));

  it('should do something', function () {
    expect(!!changePasswordService).toBe(true);
  });

});
