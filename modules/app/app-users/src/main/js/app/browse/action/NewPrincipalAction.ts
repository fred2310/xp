import '../../../api.ts';
import {NewPrincipalEvent} from '../NewPrincipalEvent';
import {UserTreeGridItem} from '../UserTreeGridItem';
import {UserItemsTreeGrid} from '../UserItemsTreeGrid';
import {ShowNewPrincipalDialogEvent} from '../ShowNewPrincipalDialogEvent';

import Action = api.ui.Action;
import i18n = api.util.i18n;

export class NewPrincipalAction extends Action {

    constructor(grid: UserItemsTreeGrid) {
        super(i18n('action.new'), 'mod+alt+n');
        this.setEnabled(false);
        this.onExecuted(() => {
            const principals: UserTreeGridItem[] = grid.getSelectedDataList();
            if (principals.length > 0) {
                new NewPrincipalEvent(principals).fire();
            } else {
                new ShowNewPrincipalDialogEvent().fire();
            }
        });
    }
}
