package com.merritt.samples.testing.encryption.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

public class EncryptForm extends ValidatorForm
{
    private static final long serialVersionUID = -1690383741595752427L;
    private String password = "";
    private String encPassword = "";
    private String dispatch = "";
    
    /**
     * @return the encPassword
     */
    public String getEncPassword()
    {
        return encPassword;
    }
    
    /**
     * @param encPassword the encPassword to set
     */
    public void setEncPassword( final String encPassword )
    {
        this.encPassword = encPassword;
    }
    
    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }
    
    /**
     * @param password the password to set
     */
    public void setPassword( final String password )
    {
        this.password = password;
    }
    
    /**
     * @return the dispatch
     */
    public String getDispatch()
    {
        return dispatch;
    }
    
    /**
     * @param dispatch the dispatch to set
     */
    public void setDispatch( final String dispatch )
    {
        this.dispatch = dispatch;
    }
    
    /**
     * Reset all properties to their default values.
     * 
     * @param mapping
     *            The mapping used to select this instance
     * @param request
     *            The servlet request we are processing
     */
    @Override
    public void reset( final ActionMapping mapping, final HttpServletRequest request )
    {
        password = "";
    }
    
}
