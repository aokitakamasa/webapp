'use strict';

describe('Directive: pwCapitalValidator', function () {

  // load the directive's module
  beforeEach(module('panelApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<pw-capital-validator></pw-capital-validator>');
    element = $compile(element)(scope);
    expect(element.text()).toBe('this is the pwCapitalValidator directive');
  }));
});
