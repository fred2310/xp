module app.wizard.action {

    export class ShowLiveEditAction extends api.ui.Action {

        constructor(wizard: app.wizard.ContentWizardPanel) {
            super("Live");

            this.setEnabled(false);
            this.onExecuted(() => {
                new ShowLiveEditEvent().fire();
                wizard.showLiveEdit();

            });
        }
    }

}
