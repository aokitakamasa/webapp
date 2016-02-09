'use strict';

/**
 * @ngdoc directive
 * @name panelApp.directive:pwSpecialValidator
 * @description
 * # pwSpecialValidator
 */
angular.module('panelApp')
  .directive('pwSpecial', function() {
    return {
      require: 'ngModel',
      link: function(scope, elem, attrs, ctrl) {
        ctrl.$validators.pwSpecial = function(modelValue, viewValue) {
          if (!viewValue) {
            viewValue = 0;
          }
          var specialsCollection = '!@#$%^&*:\'<>/?';
          var specials = 0;
          for (var i = 0; i < viewValue.length; i++) {
            for (var j = 0; j < specialsCollection.length; j++) {
              if (viewValue[i] === specialsCollection[j]) {
                specials++;
              }
            }
            if (specials >= scope.passwordConfig.minSpecial) {
              return true;
            }
          }
          return false;
        };
      }
    };
  });
