'use strict';

/**
 * @ngdoc service
 * @name panelApp.changePasswordService
 * @description
 * # changePasswordService
 * Service in the panelApp.
 */
angular.module('panelApp')
  .service('changePasswordService', function($http) {
    return function(password) {
      return $http.post('/panel/api/user/changePassword', password);
    };
  });
