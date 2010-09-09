/*
 * $Id: AppleMixin.java 205 2009-11-19 11:45:39Z ross $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

// borrowed from scriptaculous to do includes.

var _IBEANS_INCLUDE = {

  script: function(libraryName) {
    document.write('<script type="text/javascript" src="'+libraryName+'"></script>');
  },
  load: function() {
    var scriptTags = document.getElementsByTagName("script");
    for(var i=0;i<scriptTags.length;i++) {
      if(scriptTags[i].src && scriptTags[i].src.match(/ibeans\.js$/)) {
        var path = scriptTags[i].src.replace(/ibeans\.js$/,'');
        this.script(path + 'dojo/dojo.js');
        this.script(path + '_ibeans.js');
        break;
      }
    }
  }
}

_IBEANS_INCLUDE.load();