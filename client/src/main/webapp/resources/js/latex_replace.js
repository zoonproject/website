/**
 * Replace latex code with a few classed span tags.
 *
 * @param text Text to search and replace latex code.
 * @return Modified or original text.
 */
function latex_replace(text) {
  // 'link' processed first because it's nested.
  if (text.indexOf('\link') != -1) {
    // \code{\link[base]{set.seed}}
    // \code{\link{mgcv::smooth.terms}}
    text = text.replace(/\\link(.*?\})/g, function(matched, token) {
      // [base]{set.seed} -> base
      token = token.replace(/\[(.*)\]/g, function(matched, token) {
        return '<span class="square">' + token + '</span>';
      });
      // <span class="square">base</span>{set.seed} -> set.seed
      // {mgcv::smooth.terms} -> mgcv::smooth.terms
      token = token.replace(/\{(.*)\}/g, function(matched, token) {
        return '<span class="squiggly">' + token + '</span>';
      });
      return token;
    });
  }

  // \code{c(-10, 10, 45, 65)}
  if (text.indexOf('\code') != -1) {
    text = text.replace(/\\code(\{.*?\})/g, function(matched, token) {
      token = token.substring(1).substring(0, token.length - 2);
      return '<span class="preformatted">' + token + '</span>';
    });
  }

  if (text.indexOf('\\url') != -1) {
    text = text.replace(/\\url(\{.*?\})/g, function(matched, token) {
      token = token.substring(1).substring(0, token.length - 2);
      return '<a href="' + token + '">' + token + '</a>';
    });
  }

  // \emph{Thryothorus ludovicianus}
  if (text.indexOf('\emph') != -1) {
    text = text.replace(/\\emph(\{.*?\})/g, function(matched, token) {
      token = token.substring(1).substring(0, token.length - 2);
      return '<span style="font-weight: bold;">' + token + '</span>';
    });
  }

  return text;
}