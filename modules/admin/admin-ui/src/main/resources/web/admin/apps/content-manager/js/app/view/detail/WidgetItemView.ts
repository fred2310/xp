module app.view.detail {

    import Element = api.dom.Element;
    import LabelEl = api.dom.LabelEl;
    import LinkEl = api.dom.LinkEl;

    export class WidgetItemView extends api.dom.DivEl {

        public static debug = false;

        constructor(className?: string) {
            super("widget-item-view" + (className ? " " + className : ""));
        }

        public layout(): wemQ.Promise<any> {
            if (WidgetItemView.debug) {
                console.debug('WidgetItemView.layout: ', this);
            }
            return wemQ<any>(null);
        }

        private getFullWidgetUrl(url: string) {
            return url + "?uid=" + Date.now().toString();
        }

        public setUrl(url: string): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>(),
                linkEl = new LinkEl(this.getFullWidgetUrl(url)),
                el = this.getEl(),
                onLinkLoaded = ((event: UIEvent) => {
                    var mainContainer = wemjq(event.target["import"]).find("div")[0];
                    if (mainContainer) {
                        el.appendChild(document.importNode(<Node>mainContainer, true));
                    }
                    linkEl.unLoaded(onLinkLoaded);
                    deferred.resolve(null);
                });

            this.removeChildren();

            linkEl.onLoaded(onLinkLoaded);
            this.appendChild(linkEl);

            return deferred.promise;
        }
    }
}