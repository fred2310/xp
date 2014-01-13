module api.module {

    export class DeleteModuleRequest extends ModuleResourceRequest<api.module.json.ModuleJson> {

        private key:string;

        constructor(key:string) {
            super();
            super.setMethod("POST");
            this.key = key;
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "delete");
        }

        getParams():Object {
            return {
                key: this.key
            };
        }

        sendAndParse(): JQueryPromise<api.module.Module> {
            var deferred = jQuery.Deferred<api.module.Module>();

            this.send().done((response: api.rest.JsonResponse<api.module.json.ModuleJson>) => {
                if ( !response.isBlank() ) {
                    deferred.resolve(this.fromJsonToModule(response.getResult()));
                } else {
                    deferred.reject();
                }
            }).fail(() => {
                deferred.reject();
            });

            return deferred;
        }
    }
}