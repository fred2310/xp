module api {

    export class DefaultErrorHandler {

        static handle(error: any) {

            if (api.ObjectHelper.iFrameSafeInstanceOf(error, Error)) {
                console.error(error);
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(error, Exception)) {
                var message = error.getMessage();

                switch (error.getType()){
                case ExceptionType.ERROR:
                    console.error(message);
                    api.notify.showError(message);
                    break;
                case ExceptionType.WARNING:
                    console.warn(message);
                    api.notify.showWarning(message);
                    break;
                case ExceptionType.INFO:
                    console.info(message);
                    api.notify.showFeedback(message);
                    break;
                }
            } else {
                console.error(error);
                api.notify.showError(error.toString());
            }

        }

    }
}