module app.wizard.page {

    import Descriptor = api.content.page.Descriptor;
    import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;
    import Component = api.content.page.region.Component;
    import LayoutComponent = api.content.page.region.LayoutComponent;
    import LayoutRegions = api.content.page.region.LayoutRegions;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
    import ComponentPathRegionAndComponent = api.content.page.region.ComponentPathRegionAndComponent;
    import ComponentName = api.content.page.region.ComponentName;
    import PageRegions = api.content.page.PageRegions;
    import ComponentView = api.liveedit.ComponentView;

    export class ComponentNameChanger {

        private pageRegions: PageRegions;

        private componentView: ComponentView<Component>;

        setPageRegions(value: PageRegions): ComponentNameChanger {
            this.pageRegions = value;
            return this;
        }

        setComponentView(value: ComponentView<Component>): ComponentNameChanger {
            this.componentView = value;
            return this;
        }

        changeTo(name: string) {
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.componentView, "componentView cannot be null");

            var component = this.componentView.getComponent();

            var componentName = new ComponentName(name);
            component.setName(componentName);
        }
    }
}