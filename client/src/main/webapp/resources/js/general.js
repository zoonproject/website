"use strict";
Object.size = function(obj) {
  var size = 0;
  for (var key in obj) {
    if (obj.hasOwnProperty(key)) size++;
  }
  return size;
};

/**
 * Sort artifacts by name and then version.
 * 
 * @param artifacts Array of artifacts.
 */
function artifact_sorter(artifacts) {
  artifacts.sort(function(a, b) {
    var name_a = a.name;
    var version_a = a.version;
    var name_b = b.name;
    var version_b = b.version;
    return (name_a < name_b) ? -1 : (name_a > name_b) ? 1 : 
           (version_a < version_b) ? -1 : (version_a > version_b) ? 1 : 0;
  });
}