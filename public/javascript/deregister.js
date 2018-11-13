$(document).ready(function() {
  if(typeof GOVUK.ShowHideContent !== 'undefined') {
    var showHideContent = new GOVUK.ShowHideContent();
      showHideContent.init();
  }
});

function sendGAEvent(category, action, label) {
  ga('send', 'event', category, action, label);
}

$(document).ready($(function () {
  $('[data-metrics]').each(function () {
    var metrics = $(this).attr('data-metrics');
    var parts = metrics.split(':');
    sendGAEvent(parts[0], parts[1], parts[2]);
  });
}));
