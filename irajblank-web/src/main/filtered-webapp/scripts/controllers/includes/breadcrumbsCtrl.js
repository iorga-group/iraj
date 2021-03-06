'use strict';

angular.module('blank-iraj')
.controller('BreadcrumbsCtrl', function ($scope, $location, irajBreadcrumbsService) {
	$scope.breadcrumbs = irajBreadcrumbsService.getAll();
	
	$scope.goToUrl = function goToUrl(path, index){
		irajBreadcrumbsService.switchTo(index);
		$location.path(path);
	};
	
	$scope.$on('iraj:breadcrumbs-refresh', function() {
		$scope.breadcrumbs = irajBreadcrumbsService.getAll();
    });
});