'use strict';

describe('Directive: pwLengthValidator', function () {

  // load the directive's module
  beforeEach(module('panelApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<pw-length-validator></pw-length-validator>');
    element = $compile(element)(scope);
    expect(element.text()).toBe('this is the pwLengthValidator directive');
  }));
});
