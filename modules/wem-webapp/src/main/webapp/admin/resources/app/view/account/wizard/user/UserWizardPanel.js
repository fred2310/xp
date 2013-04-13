Ext.define('Admin.view.account.wizard.user.UserWizardPanel', {
    extend: 'Admin.view.WizardPanel',
    alias: 'widget.userWizardPanel',
    requires: [
        'Admin.view.account.wizard.user.UserStoreListPanel',
        'Admin.view.account.wizard.user.UserWizardToolbar',
        'Admin.view.account.EditUserFormPanel',
        'Admin.view.account.wizard.user.WizardStepLoginInfoPanel',
        'Admin.view.account.wizard.user.WizardStepMembershipPanel',
        'Admin.view.SummaryTreePanel',
        'Admin.plugin.fileupload.PhotoUploadButton'
    ],

    border: 0,
    autoScroll: true,

    defaults: {
        border: false
    },

    headerTemplate: '<div class="admin-wizard-userstore">' +
                    '<label>{label}: </label>' +
                    '<span>{userstore}</span>' +
                    '<span>{qualifiedName}</span>' +
                    '</div>',

    initComponent: function () {
        var me = this;
        var isNew = this.isNewUser();

        if (me.data) {
            this.preProcessAddresses(me.data);
        }
        me.headerData = this.resolveHeaderData(me.data);
        me.autogenerateDisplayName = isNew;

        me.tbar = {
            xtype: 'userWizardToolbar',
            isNewUser: isNew
        };

        this.callParent(arguments);

        var uploader = this.down('photoUploadButton');
        uploader.on('fileuploaded', me.photoUploaded, me);


        // Make wizard navigation sticky
        me.on('afterrender', function (userWizard) {
            this.addStickyNavigation(userWizard);
            //Render all user forms
            me.renderUserForms(me.data && me.data.userStore ? me.data.userStore : me.userstore);
            me.removeEmptySteps(userWizard);
            // Set active item to first one. D-02010 bug workaround
            me.wizard.getLayout().setActiveItem(0);
        });
    },


    resolveHeaderData: function (data) {

        var isNew = this.isNewUser();

        return {
            displayName: isNew ? 'Display Name' : data.displayName,
            qualifiedName: isNew ? this.userstore + '\\' : data.qualifiedName,
            label: isNew ? "New User" : "User",
            photoUrl: isNew ? 'rest/account/image/default/user' : data.image_url,
            userGroups: isNew ? [] : data.memberships,
            edited: false
        };
    },

    preProcessAddresses: function (data) {
        // assign each address a position to be able to reflect this in diff
        if (data.profile && data.profile.addresses) {
            var i;
            for (i = 0; i < data.profile.addresses.length; i++) {
                data.profile.addresses[i].originalIndex = i;
            }
        }
    },

    removeEmptySteps: function (wizardPanel) {
        wizardPanel.wizard.items.each(function (item) {
            if (!item.alwaysKeep && item.getForm && (item.getForm().getFields().getCount() === 0)) {
                wizardPanel.wizard.remove(item);
            }
        });
    },

    addStickyNavigation: function (wizardPanel) {
        wizardPanel.body.on('scroll', function () {
            // Ideally the element should be cached, but the navigation view is rendered (tpl.update()) for each step.
            var navigationElement = Ext.get(Ext.DomQuery.selectNode('.admin-wizard-navigation-container',
                wizardPanel.body.dom));
            var bodyScrollTop = wizardPanel.body.getScroll().top;
            if (bodyScrollTop > 73) {
                navigationElement.addCls('admin-wizard-navigation-container-sticky');
            } else {
                navigationElement.removeCls('admin-wizard-navigation-container-sticky');
            }
        }, wizardPanel);
    },

    resizeFileUpload: function (file) {
        file.el.down('input[type=file]').setStyle({
            width: file.getWidth(),
            height: file.getHeight()
        });
    },

    setFileUploadDisabled: function (disable) {
        //TODO: disable image upload
        //this.uploadForm.setDisabled( disable );
    },

    renderUserForms: function (userStore) {
        var userForms = this.query('editUserFormPanel');
        Ext.Array.each(userForms, function (userForm) {
            userForm.renderUserForm({userStore: userStore});
        });
    },

    isNewUser: function () {
        return Ext.isEmpty(this.data);
    },

    getWizardHeader: function () {
        return this.down('wizardHeader');
    },

    getData: function () {
        var data = Ext.apply(this.callParent(), this.getWizardHeader().getData());
        if (this.data) {
            data.key = this.data.key;
        }
        return data;
    },

    photoUploaded: function (photoUploadButton, response) {
        var photoRef = response.items && response.items.length > 0 && response.items[0].id;
        this.addData({imageRef: photoRef});
    },

    createSteps: function () {
        return [
            {
                stepTitle: "Profile",
                itemId: "profilePanel",
                xtype: 'editUserFormPanel',
                data: this.data,
                enableToolbar: false
            },
            {
                stepTitle: "User",
                itemId: "userPanel",
                xtype: 'editUserFormPanel',
                data: this.data,
                includedFields: ['name', 'email', 'password', 'repeatPassword', 'photo',
                    'country', 'locale', 'timezone', 'globalPosition'],
                enableToolbar: false
            },
            {
                stepTitle: "Places",
                itemId: 'placesPanel',
                xtype: 'editUserFormPanel',
                includedFields: ['address'],
                data: this.data,
                enableToolbar: false
            },
            {
                stepTitle: "Memberships",
                groups: this.headerData.userGroups,
                xtype: 'wizardStepMembershipPanel',
                listeners: {
                    afterrender: {
                        fn: function () {
                            var membershipPanel = this.down('wizardStepMembershipPanel');
                            this.addData(membershipPanel.getData());
                        },
                        scope: this
                    }
                }
            },
            {
                stepTitle: 'Summary',
                dataType: 'user',
                xtype: 'summaryTreePanel'
            }
        ];
    },

    createIcon: function () {
        var me = this;

        return {
            xtype: 'container',
            width: 110,
            height: 110,
            margin: '0 10 0 0',
            items: [
                {
                    xtype: 'photoUploadButton',
                    width: 110,
                    height: 110,
                    photoUrl: this.headerData.photoUrl,
                    title: "User",
                    progressBarHeight: 6,
                    listeners: {
                        mouseenter: function () {
                            var imageToolTip = me.down('#imageToolTip');
                            imageToolTip.show();
                        },
                        mouseleave: function () {
                            var imageToolTip = me.down('#imageToolTip');
                            imageToolTip.hide();
                        }
                    }
                },
                {
                    styleHtmlContent: true,
                    style: {
                        top: '5px',
                        zIndex: 1001
                    },
                    height: 50,
                    border: 0,
                    itemId: 'imageToolTip',
                    cls: 'admin-image-upload-button-image-tip',
                    html: '<div class="x-tip x-tip-default x-layer" role="tooltip">' +
                          '<div class="x-tip-anchor x-tip-anchor-top"></div>' +
                          '<div class="x-tip-body  x-tip-body-default x-tip-body-default">' +
                          'Click to upload photo</div></div>',
                    listeners: {
                        afterrender: function (cmp) {
                            Ext.Function.defer(function () {
                                cmp.hide();
                            }, 10000);
                        }
                    }
                }
            ]
        };
    },

    createWizardHeader: function () {
        var wizardHeader = Ext.create('Admin.view.WizardHeader', {
            xtype: 'wizardHeader',
            pathConfig: {
                hidden: true
            },
            nameProperty: 'qualifiedName',
            nameConfig: {
                readOnly: true,
                stripCharsRe: /[^a-z0-9\-:]+/ig,
                vtype: 'qualifiedName'
            },
            data: this.data
        });
        return wizardHeader;

    },

    createActionButton: function () {
        return {
            xtype: 'button',
            text: 'Save',
            action: 'saveUser'
        };
    }

});
