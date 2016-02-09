'use strict';

describe('Service: changeTerminalNameService', function () {

  // load the service's module
  beforeEach(module('panelApp'));

  // instantiate service
  var changeTerminalNameService;
  beforeEach(inject(function (_changeTerminalNameService_) {
    changeTerminalNameService = _changeTerminalNameService_;
  }));

  it('should do something', function () {
    expect(!!changeTerminalNameService).toBe(true);
  });

});
