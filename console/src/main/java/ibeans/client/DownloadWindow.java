/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package ibeans.client;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * TODO
 */
public class DownloadWindow extends FormPanel
{
    public DownloadWindow(final String user, final String pass, final String id, final String version, final Window window, final IBeansConsole2 console)
    {
        window.setSize(400, 400);
        window.setPlain(true);
        window.setModal(true);
        window.setBlinkModal(true);
        window.setHeading("Log in");
        window.setLayout(new FitLayout());

        // Because we're going to add a FileUpload widget, we'll need to set the
        // form to use the POST method, and multipart MIME encoding.
        //setEncoding(FormPanel.ENCODING_MULTIPART);
        setMethod(Method.POST);
        setHeading("Please Log in to iBeans Central");
        add(new Html("You need to sign in using your <a href='http://mulesoft.org' target='_blank'>Mule Community</a> account.  If you haven't signed up you can do so <a href='http://www.mulesoft.org/register/newuser.mule' target='_blank'>here</a> (it only takes a few seconds).  You can get a password reminder <a href='http://www.mulesoft.org/login.action' target='_blank'>here</a>.<p>&nbsp;</p>"));

        TextField<String> username = new TextField<String>();
        username.setName("username");
        username.setId("username");
        username.setFieldLabel("Username");
        username.setAllowBlank(false);
        username.setValue(user);
        add(username);

        TextField<String> password = new TextField<String>();
        password.setName("password");
        password.setId("password");
        password.setFieldLabel("Password");
        password.setAllowBlank(false);
        password.setPassword(true);
        password.setValue(pass);

        add(password);

        HiddenField<String> ibeanID = new HiddenField<String>();
        ibeanID.setName("ibean-id");
        ibeanID.setValue(id);
        add(ibeanID);

        HiddenField<String> ibeanVersion = new HiddenField<String>();
        ibeanVersion.setName("ibean-version");
        ibeanVersion.setValue(version);
        add(ibeanVersion);

        Button b = new Button("Submit", new SelectionListener<ButtonEvent>()
        {
            public void componentSelected(ButtonEvent ce)
            {
                String u = (String) DownloadWindow.this.getFields().get(0).getValue();
                String p = (String) DownloadWindow.this.getFields().get(1).getValue();
                console.getUserInfo().setUser(u);
                console.getUserInfo().setPass(p);
                console.saveUserInfo(console.getUserInfo());
                if (id != null)
                {
                    //We're not just setting username and password, we're downloading the artifact t

                    IBeansCentralServiceAsync service = console.getRepositoryService();
                    service.downloadIBean(u, p, id, version, new AbstractAsyncCallback<String>(console)
                    {
                        public void onSuccess(String s)
                        {
                            console.updateStatus(Status.INFO, s);
                        }
                    });
                }
                window.hide();
            }
        });
        addButton(b);

        addButton(new Button("Cancel", new SelectionListener<ButtonEvent>()
        {
            public void componentSelected(ButtonEvent ce)
            {
                window.hide();
            }
        }));

        setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(this);
        binding.addButton(b);
    }


}
