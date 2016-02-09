'use strict';

describe('Controller: EditServicesFormCtrl', function () {

  // load the controller's module
  beforeEach(module('panelApp'));

  var EditServicesFormCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    EditServicesFormCtrl = $controller('EditServicesFormCtrl', {
      $scope: scope
      // place here mocked dependencies
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(EditServicesFormCtrl.awesomeThings.length).toBe(3);
  });
});
