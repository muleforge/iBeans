/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package ibeans.client;

/**
 * TODO Need an admin service and the WScript objects
 */
public class ShellPanel// extends LayoutContainer implements KeyboardListener
{

//
//    private Tree scriptTree;
//    private FlexTable table;
//    private Button evaluateBtn;
//    private CheckBox saveAsCB;
//    private TextField<String> saveAsTB;
//    private Button saveBtn;
//    private Button deleteBtn;
//    private Button clearBtn;
//    private TextArea scriptArea;
//    private Label scriptResultsLabel;
//    private CheckBox loadOnStartupCB;
//    private IBeansConsole2 console;
//
//
//    public ShellPanel(IBeansConsole2 console)
//    {
//
//        this.console = console;
//
//        initLocalWidgets();
//
//        scriptArea.setText(null);
//
//        // text area to paste script into
//        table.setWidget(0, 0, createPrimaryTitle("Galaxy Admin Shell"));
//        table.setWidget(1, 0, new Label("Type or paste a Groovy script to be executed on the server. A return value will be displayed below the area. " +
//                "Tips: \n   Spring's context is available as 'applicationContext' variable" +
//                "\n   only String return values are supported (or null)"));
//        table.getFlexCellFormatter().setColSpan(1, 0, 2);
//        table.getCellFormatter().setWidth(1, 0, "500px");
//        table.setWidget(2, 0, scriptArea);
//
//        this.createScriptTree();
//        VerticalPanel vp = new VerticalPanel();
//        vp.add(createTitleText("Saved Scripts"));
//        vp.add(scriptTree);
//
//        table.setWidget(2, 1, vp);
//        table.getCellFormatter().setVerticalAlignment(2, 1, HasAlignment.ALIGN_TOP);
//        table.getCellFormatter().setHorizontalAlignment(2, 1, HasAlignment.ALIGN_LEFT);
//
//        // script results
//        FlowPanel scriptOutputPanel = new FlowPanel();
//        scriptResultsLabel.setWordWrap(false);
//        scriptOutputPanel.add(scriptResultsLabel);
//
//        // add user control buttons in a table
//        FlexTable execButtonTable = new FlexTable();
//        execButtonTable.setWidget(0, 0, evaluateBtn);
//        execButtonTable.setWidget(0, 1, clearBtn);
//
//        table.setWidget(3, 0, execButtonTable);
//
//        FlexTable persistButtonTable = new FlexTable();
//        persistButtonTable.setWidget(0, 0, loadOnStartupCB);
//        persistButtonTable.setWidget(0, 1, saveAsCB);
//        persistButtonTable.setWidget(0, 2, saveAsTB);
//        persistButtonTable.setWidget(0, 3, saveBtn);
//        persistButtonTable.setWidget(0, 4, deleteBtn);
//
//        table.setWidget(4, 0, persistButtonTable);
//
//        // results of script execution
//        table.setWidget(5, 0, scriptOutputPanel);
//
//        add(table);
//        saveBtn.setEnabled(true);
//    }
//
//public static Widget createPrimaryTitle(String title) {
//        Label label = new Label(title);
//        label.setStyleName("title");
//        return label;
//    }
//
//      public static Label createTitleText(String title) {
//        Label label = new Label(title);
//        label.setStyleName("right-title");
//        return label;
//    }
//
//    private void initLocalWidgets() {
//        // create objects, set initial state including listeners
//        // display existing scripts in a tree
//        scriptTree = new Tree();
//
//        // main page layout
//        table = new FlexTable();
//
//        scriptResultsLabel = new Label();
//
//        final SelectionListener<ButtonEvent> buttonListner = new SelectionListener<ButtonEvent>() {
//            public void componentSelected(ButtonEvent buttonEvent) {
//                Button sender = buttonEvent.getButton();
//
//                if (sender == saveBtn) {
//                    save();
//                }
//
//                if (sender == deleteBtn) {
//                    final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
//                        public void handleEvent(MessageBoxEvent ce) {
//                            com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();
//
//                            if (Dialog.YES.equals(btn.getItemId())) {
//                                delete();
//                            }
//                        }
//                    };
//                    MessageBox.confirm("Confirm", "Are you sure you want to delete this script?", l);
//                }
//
//
//                if (sender == clearBtn) {
//                   // adminPanel.clearErrorMessage();
//                    refresh();
//                }
//
////                if (sender == evaluateBtn) {
////                    evaluateBtn.setEnabled(false);
////                    adminPanel.getGalaxy().getAdminService().executeScript(scriptArea.getText(), new AbstractCallback(adminPanel) {
////                        public void onFailure(Throwable caught) {
////                            evaluateBtn.setEnabled(true);
////                            scriptResultsLabel.setText("");
////                            super.onFailure(caught);
////                        }
////
////                        public void onSuccess(Object o) {
////                            adminPanel.clearErrorMessage();
////                            evaluateBtn.setEnabled(true);
////                            scriptResultsLabel.setText(o == null ? "No value returned" : o.toString());
////                        }
////                    });
////
////                }
//
//            }
//        };
//
//        saveBtn = new Button("Save", buttonListner);
//        deleteBtn = new Button("Delete", buttonListner);
//        clearBtn = new Button("Reset", buttonListner);
//        evaluateBtn = new Button("Evaluate", buttonListner);
//
//        saveAsCB = new CheckBox(" Save As... ");
//        saveAsCB.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                saveAsTB.setEnabled(true);
//                if (saveAsCB.isChecked()) {
//                    saveAsTB.focus();
//                    saveAsTB.setAllowBlank(false);
//                } else {
//                    saveAsTB.clearInvalid();
//                }
//            }
//        });
//
//        saveAsTB = new TextField<String>();
//        saveAsTB.setEnabled(true);
//
//        loadOnStartupCB = new CheckBox(" Run on startup ");
//
//        // where the scripts are pasted into
//        scriptArea = new TextArea();
//        scriptArea.setCharacterWidth(80);
//        scriptArea.setVisibleLines(30);
//
//    }
//


//    protected void createScriptTree() {
//        scriptTree = new Tree();
//        scriptTree.addTreeListener(new TreeListener() {
//            public void onTreeItemSelected(TreeItem ti) {
//                WScript ws = (WScript) ti.getUserObject();
//                scriptArea.setText(ws.getScript());
//                loadOnStartupCB.setChecked(ws.isRunOnStartup());
//            }
//
//            public void onTreeItemStateChanged(TreeItem ti) {
//            }
//        });
//
//        console.getAdminService().getScripts(new AsyncCallback<List<WScript>>() {
//            public void onFailure(Throwable caught) {
//                console.errorStatus(caught);
//            }
//
//            public void onSuccess(List<WScript> o) {
//                addTreeItems(o);
//            }
//        });
//    }
//
//    private void addTreeItems(List<WScript> scripts) {
//        for (WScript script : scripts) {
//            TreeItem treeItem = scriptTree.addItem(script.getName());
//            treeItem.setUserObject(script);
//        }
//    }
//
//
//    protected void refresh() {
//        //doShowPage();
//        saveAsCB.setChecked(false);
//        saveAsTB.setValue(null);
//        loadOnStartupCB.setChecked(false);
//    }
//
//
//    private void save() {
//
//        // validate script name
//        if (scriptTree.getItemCount() > 0 && scriptTree.getSelectedItem() == null
//                && !saveAsTB.validate()) {
//            return;
//        }
//
//        saveBtn.setEnabled(false);
//        WScript ws = new WScript();
//
//        // try and get it from the tree first
//        final TreeItem ti = scriptTree.getSelectedItem();
//        if (ti != null) {
//            ws = (WScript) ti.getUserObject();
//        }
//
//        if (saveAsCB.isChecked()) {
//            ws.setName(saveAsTB.getValue());
//            // save as should null out the Id so it creates a new copy
//            ws.setId(null);
//        }
//        ws.setScript(scriptArea.getText());
//        ws.setRunOnStartup(loadOnStartupCB.isChecked());
//
//        // a local ref to satisfy anonymous inner class requirements
//        final WScript localCopyWs = ws;
//        console.getAdminService().save(ws, new AsyncCallback() {
//            public void onFailure(Throwable caught) {
//                saveBtn.setEnabled(true);
//                console.errorStatus(caught);
//            }
//
//            public void onSuccess(Object o) {
//                saveBtn.setEnabled(true);
//                console.updateStatus(Status.INFO, "Script '" + localCopyWs.getName() + "' has been saved");
//                refresh();
//                // if it was not a New script, redisplay it in the window.
//                if (ti != null) {
//                    scriptTree.setSelectedItem(ti, true);
//                }
//            }
//        });
//
//
//    }
//
//
//    private void delete() {
//        TreeItem ti = scriptTree.getSelectedItem();
//        final WScript wsx = (WScript) ti.getUserObject();
//
//        deleteBtn.setEnabled(false);
//        console.getAdminService().deleteScript(wsx.getId(), new AsyncCallback() {
//            public void onFailure(Throwable caught) {
//                deleteBtn.setEnabled(true);
//                console.errorStatus(caught);
//            }
//
//            public void onSuccess(Object o) {
//                deleteBtn.setEnabled(true);
//                scriptArea.setText(null);
//                console.updateStatus(Status.INFO, "Script '" + wsx.getName() + "' has been deleted");
//                refresh();
//            }
//        });
//    }
//
//    public void onKeyPress(Widget widget, char keyCode, int modifiers) {
//        if ((keyCode == KEY_ENTER) && (modifiers == 0)) {
//            save();
//        }
//    }
//
//
//    public void onKeyDown(Widget widget, char c, int i) {
//    }
//
//    public void onKeyUp(Widget widget, char c, int i) {
//    }
}
