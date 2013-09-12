module LiveEdit.component.mouseevent {

    // Uses
    var $ = $liveEdit;

    export class Base {
        public componentCssSelectorFilter:string = '';

        constructor() {
        }

        attachMouseOverEvent():void {

            $(document).on('mouseover', this.componentCssSelectorFilter, (event:JQueryEventObject) => {
                if (this.cancelMouseOverEvent(event)) {
                    return;
                }
                event.stopPropagation();

                LiveEdit.Selection.clearSelection();

                var component:LiveEdit.component.Component = new LiveEdit.component.Component($(event.currentTarget));

                $(window).trigger('mouseOverComponent.liveEdit', [ component ]);
            });
        }

        attachMouseOutEvent():void {
            $(document).on('mouseout', () => {
                if (LiveEdit.Selection.pageHasSelectedElements()) {
                    return;
                }
                $(window).trigger('mouseOutComponent.liveEdit');
            });
        }

        attachClickEvent():void {

            $(document).on('click contextmenu touchstart', this.componentCssSelectorFilter, (event:JQueryEventObject) => {
                if (this.targetIsLiveEditUiComponent($(event.target))) {
                    return;
                }

                // Make sure the event is not propagated to any parent
                event.stopPropagation();

                // Needed so the browser's context menu is not shown on contextmenu
                event.preventDefault();

                var component = new LiveEdit.component.Component($(event.currentTarget)),
                    deselectComponent:boolean = component.isSelected() || LiveEdit.Selection.pageHasSelectedElements();

                // Toggle select/deselect
                if (deselectComponent) {
                    LiveEdit.Selection.clearSelection();
                    $(window).trigger('deselectComponent.liveEdit');
                } else {
                    LiveEdit.Selection.setSelectionAttributeOnElement(component.getElement());

                    // Used by for menu positioning
                    // fixme: send event and let menu do this by itself
                    var pagePosition:any = {
                        x: event.pageX,
                        y: event.pageY
                    };

                    $(window).trigger('selectComponent.liveEdit', [component, pagePosition]);
                }
            });
        }

        // fixme: move when empty placeholder stuff is refactored
        getAll():JQuery {
            return $(this.componentCssSelectorFilter);
        }

        cancelMouseOverEvent(event:JQueryEventObject):boolean {
            return this.targetIsLiveEditUiComponent($(event.target)) || LiveEdit.Selection.pageHasSelectedElements() ||
                   LiveEdit.component.DragDropSort.isDragging();
        }

        private targetIsLiveEditUiComponent(target:JQuery):boolean {
            return target.is('[id*=live-edit-ui-cmp]') || target.parents('[id*=live-edit-ui-cmp]').length > 0;
        }

    }
}
