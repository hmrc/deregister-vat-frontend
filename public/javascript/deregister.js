$(document).ready(function() {

    if(typeof GOVUK.ShowHideContent !== 'undefined') {
    var showHideContent = new GOVUK.ShowHideContent();
      showHideContent.init();
    }
    // =====================================================
    // Use GOV.UK shim-links-with-button-role.js to trigger a link styled to look like a button,
    // with role="button" when the space key is pressed.
    // =====================================================
    GOVUK.shimLinksWithButtonRole.init();
});

