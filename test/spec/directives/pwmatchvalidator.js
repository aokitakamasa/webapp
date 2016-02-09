'use strict';

describe('Directive: pwMatchValidator', function () {

  // load the directive's module
  beforeEach(module('panelApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<pw-match-validator></pw-match-validator>');
    element = $compile(element)(scope);
    expect(element.text()).toBe('this is the pwMatchValidator directive');
  }));
});
