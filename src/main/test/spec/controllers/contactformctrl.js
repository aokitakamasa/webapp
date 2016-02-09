'use strict';

describe('Controller: ContactFormCtrl', function () {

  // load the controller's module
  beforeEach(module('panelApp'));

  var ContactFormCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    ContactFormCtrl = $controller('ContactFormCtrl', {
      $scope: scope
      // place here mocked dependencies
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(ContactFormCtrl.awesomeThings.length).toBe(3);
  });
});
