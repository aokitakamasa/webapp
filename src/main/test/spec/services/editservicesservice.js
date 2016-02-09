'use strict';

describe('Service: editServicesService', function () {

  // load the service's module
  beforeEach(module('panelApp'));

  // instantiate service
  var editServicesService;
  beforeEach(inject(function (_editServicesService_) {
    editServicesService = _editServicesService_;
  }));

  it('should do something', function () {
    expect(!!editServicesService).toBe(true);
  });

});
