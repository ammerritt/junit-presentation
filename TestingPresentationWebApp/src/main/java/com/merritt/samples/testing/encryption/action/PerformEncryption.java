package com.merritt.samples.testing.encryption.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.merritt.samples.testing.encryption.form.EncryptForm;

public class PerformEncryption extends DispatchAction
{
    /**
     * Dispatch control to the "success" forward.
     */
    public ActionForward encrypt( final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response ) throws Exception
    {
        final String forwardValue = "success";
        final EncryptForm encryptForm = (EncryptForm)form;
        final PasswordEncrypter pe = new PasswordEncrypter();
        final String password = encryptForm.getPassword();
        if( password != null )
        {
            final String encpw = pe.encrypt( password );
            encryptForm.setEncPassword( encpw );
            encryptForm.setPassword( "" );
        }
        return mapping.findForward( forwardValue );
    }
    
    public ActionForward decrypt( final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response ) throws Exception
    {
        final String forwardValue = "success";
        final EncryptForm encryptForm = (EncryptForm)form;
        final PasswordEncrypter pe = new PasswordEncrypter();
        final String encPassword = encryptForm.getEncPassword();
        if( encPassword != null )
        {
            final String passwd = pe.decrypt( encPassword );
            encryptForm.setPassword( passwd );
        }
        return mapping.findForward( forwardValue );
    }
}