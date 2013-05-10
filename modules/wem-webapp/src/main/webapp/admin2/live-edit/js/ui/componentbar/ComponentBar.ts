interface ComponentsJson {
    totalCount:number;
    componentGroups:ComponentGroup[];
}

interface ComponentGroup {
    name:string;
    components:Component[];
}

interface Component {
    key:string;
    type:string;
    name:string;
    subtitle:string;
    icon:string;
}

module LiveEdit.ui {
    var $ = $liveedit;

    export class ComponentBar extends LiveEdit.ui.Base {

        private BAR_WIDTH:number;
        private TOGGLE_WIDTH:number;
        private INNER_WIDTH:number;
        private hidden:Boolean;

        constructor() {
            super();
            this.BAR_WIDTH = 235;
            this.TOGGLE_WIDTH = 30;
            this.INNER_WIDTH = this.BAR_WIDTH - this.TOGGLE_WIDTH;
            this.hidden = true;

            this.addView();
            this.loadComponentsData();
            this.registerGlobalListeners();
            this.registerEvents();

            console.log('ComponentBar instantiated. Using jQuery ' + $().jquery);
        }


        getComponentsDataUrl() {
            return '../../../admin2/live-edit/data/mock-components.json';
        }


        addView() {
            var me = this;

            var html = '';
            html += '<div class="live-edit-components-container live-edit-collapsed" style="width:' + this.BAR_WIDTH + 'px; right: -' + this.INNER_WIDTH + 'px">';
            html += '    <div class="live-edit-toggle-components-container" style="width:' + this.TOGGLE_WIDTH + 'px"><span class="live-edit-toggle-text-container">Toolbar</span></div>';
            html += '        <div class="live-edit-components">';
            html += '            <div class="live-edit-form-container">';
            html += '               <form onsubmit="return false;">';
            html += '                   <input type="text" placeholder="Filter" name="filter"/>';
            html += '               </form>';
            html += '            </div>';
            html += '            <ul>';
            html += '            </ul>';
            html += '        </div>';
            html += '    </div>';
            html += '</div>';

            me.createElement(html);
            me.appendTo($('body'));
        }


        registerGlobalListeners() {
            var me = this;
            $(window).on('component.onSelect', $.proxy(me.fadeOut, me));
            $(window).on('component.onDeselect', $.proxy(me.fadeIn, me));
            $(window).on('component.onDragStart', $.proxy(me.fadeOut, me));
            $(window).on('component.onDragStop', $.proxy(me.fadeIn, me));
            $(window).on('component.onSortStop', $.proxy(me.fadeIn, me));
            $(window).on('component.onSortStart', $.proxy(me.fadeOut, me));
            $(window).on('component.onSortUpdate', $.proxy(me.fadeIn, me));
            $(window).on('component.onRemove', $.proxy(me.fadeIn, me));
        }


        registerEvents() {
            var me = this;

            me.getToggle().click(function () {
                me.toggle();
            });

            me.getFilterInput().on('keyup', function () {
                me.filterList($(this).val());
            });

            me.getBar().on('mouseover', function () {
                $(window).trigger('componentBar:mouseover');
            });
        }


        loadComponentsData() {
            var me = this;
            $.getJSON(me.getComponentsDataUrl(), null, function (data, textStatus, jqXHR) {
                me.renderComponents(data);
                $(window).trigger('componentBar.dataLoaded');
            });
        }


        renderComponents(jsonData:ComponentsJson) {
            var me = this,
                $container = me.getComponentsContainer(),
                groups = jsonData.componentGroups;

            $.each(groups, function (index, group:ComponentGroup) {
                me.addHeader(group);
                if (group.components) {
                    me.addComponentsToGroup(group.components)
                }
            });
        }


        addHeader(componentGroup:ComponentGroup) {
            var me = this,
                html = '';
            html += '<li class="live-edit-component-list-header">';
            html += '    <span>' + componentGroup.name + '</span>';
            html += '</li>';

            me.getComponentsContainer().append(html);
        }


        addComponentsToGroup(components:Component[]) {
            var me = this;
            $.each(components, function (index, component:Component) {
                me.addComponent(component);
            });
        }


        addComponent(component:Component) {
            var me = this,
                html = '';
            html += '<li class="live-edit-component" data-live-edit-component-key="' + component.key + '" data-live-edit-component-name="' + component.name + '" data-live-edit-component-type="' + component.type + '">';
            html += '    <img src="' + component.icon + '"/>';
            html += '    <div class="live-edit-component-text">';
            html += '        <div class="live-edit-component-text-name">' + component.name + '</div>';
            html += '        <div class="live-edit-component-text-subtitle">' + component.subtitle + '</div>';
            html += '    </div>';
            html += '</li>';

            me.getComponentsContainer().append(html);
        }


        filterList(value) {
            var me = this,
                $element,
                name,
                valueLowerCased = value.toLowerCase();
            me.getComponentList().each(function (index) {
                $element = $(this);
                name = $element.data('live-edit-component-name').toLowerCase();
                $element.css('display', name.indexOf(valueLowerCased) > -1 ? '' : 'none');
            });
        }


        toggle() {
            var me = this;
            if (me.hidden) {
                me.show();
                me.hidden = false;
            } else {
                me.hide();
                me.hidden = true;
            }
        }


        show() {
            var me = this;
            var $bar = me.getBar();
            $bar.css('right', '0');
            me.getToggleTextContainer().text('');
            $bar.removeClass('live-edit-collapsed');
        }


        hide() {
            var me = this;
            var $bar = me.getBar();
            $bar.css('right', '-' + this.INNER_WIDTH + 'px');
            me.getToggleTextContainer().text('Toolbar');
            $bar.addClass('live-edit-collapsed');
        }


        fadeIn(event, triggerConfig) {
            // componenttip/menu.js triggers a component.onDeselect event
            // which results in that the bar is faded in (see the listeners above)
            // The triggerConfig is a temporary workaround until we get this right.
            if (triggerConfig && triggerConfig.showComponentBar === false) {
                return;
            }
            this.getBar().fadeIn(120);
        }


        fadeOut(event) {
            this.getBar().fadeOut(120);
        }


        getBar() {
            return this.getEl();
        }


        getToggle() {
            return $('.live-edit-toggle-components-container', this.getEl());
        }


        getFilterInput() {
            return $('.live-edit-form-container input[name=filter]', this.getEl());
        }


        getComponentsContainer() {
            return $('.live-edit-components ul', this.getEl());
        }


        getComponentList() {
            return $('.live-edit-component', this.getEl());
        }


        getToggleTextContainer() {
            return $('.live-edit-toggle-text-container', this.getEl());
        }


    }
}