'use strict';

/**
 * @ngdoc service
 * @name panelApp.editServicesService
 * @description
 * # editServicesService
 * Service in the panelApp.
 */
angular.module('panelApp')
  .service('editServicesService', function($http) {
    return function(message) {
      return $http.post('/panel/api/otrs/sendIssue', message);
    };
  });
