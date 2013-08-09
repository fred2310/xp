module api_ui {

    export class TextInput extends api_dom.InputEl {

        static MIDDLE:string = 'middle';
        static LARGE:string = 'large';

        /**
         * Specifies RegExp for characters that will be removed during input.
         */
        private stripCharsRe: RegExp;

        /**
         * Input value before it was changed by last input event.
         */
        private oldValue:string = "";

        private valueChangedEventListeners: Function[] = [];

        constructor(idPrefix?:string, className?:string, size?:string = TextInput.MIDDLE) {
            super(idPrefix, className);

            this.getEl().setAttribute('type', 'text');
            this.addClass('text-input').addClass(size);

            this.getEl().addEventListener('keypress', (event) => {
                if (!this.stripCharsRe) {
                    return;
                }

                var symbol = String.fromCharCode((<any> event).charCode);

                // prevent input of forbidden symbols
                if (this.containsForbiddenChars(symbol)) {
                    event.preventDefault();
                    return false;
                }
            });

            this.getEl().addEventListener('input', () => {
                this.fireValueChangedEvent(this.oldValue, this.getValue());
                this.oldValue = this.getValue();
            });
        }

        static large(idPrefix?:string, className?:string):TextInput {
            return new TextInput(idPrefix, className, TextInput.LARGE);
        }

        static middle(idPrefix?:string, className?:string):TextInput {
            return new TextInput(idPrefix, className, TextInput.MIDDLE);
        }

        setValue(value:string):TextInput {
            var oldValue = this.getValue();
            var newValue = this.removeForbiddenChars(value);

            if (oldValue != newValue) {
                super.setValue(newValue);
                this.fireValueChangedEvent(oldValue, newValue);
                // save new value to know which value was before input event.
                this.oldValue = newValue;
            }

            return this;
        }

        setName(value:string):TextInput {
            super.setName(value);
            return this;
        }

        setPlaceholder(value:string):TextInput {
            this.getEl().setAttribute('placeholder', value);
            return this;
        }

        getPlaceholder():string {
            return this.getEl().getAttribute('placeholder');
        }

        setForbiddenCharsRe(re: RegExp): TextInput {
            this.stripCharsRe = re;
            return this;
        }

        addValueChangedEventListener(listener:(oldValue:string, newValue:string) => void) {
            this.valueChangedEventListeners.push(listener);
        }

        private fireValueChangedEvent(oldValue:string, newValue:string) {
            this.valueChangedEventListeners.forEach((listener: (oldValue:string, newValue:string) => void) => {
                listener(oldValue, newValue);
            });
        }

        private removeForbiddenChars(rawValue: string): string {
            return this.stripCharsRe ? (rawValue || '').replace(this.stripCharsRe, '') : rawValue;
        }

        private containsForbiddenChars(value: string): bool {
            // create new RegExp object in order not to mess RegExp.lastIndex
            var forbidden = new RegExp(<any> this.stripCharsRe);
            return forbidden.test(value);
        }
    }
}