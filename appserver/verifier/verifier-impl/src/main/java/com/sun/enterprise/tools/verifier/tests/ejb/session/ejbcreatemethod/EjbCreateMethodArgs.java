/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.enterprise.tools.verifier.tests.ejb.session.ejbcreatemethod;

import com.sun.enterprise.tools.verifier.Result;
import com.sun.enterprise.tools.verifier.Verifier;
import com.sun.enterprise.tools.verifier.VerifierTestContext;
import com.sun.enterprise.tools.verifier.tests.ComponentNameConstructor;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbCheck;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import com.sun.enterprise.tools.verifier.tests.ejb.RmiIIOPUtils;
import org.glassfish.ejb.deployment.descriptor.EjbDescriptor;
import org.glassfish.ejb.deployment.descriptor.EjbSessionDescriptor;

import java.lang.reflect.Method;

/** 
 * Session Bean's ejbCreate(...) methods arguments test.
 * Each session Bean class must define one or more ejbCreate(...) methods. 
 * The number and signatures of a session Bean's create methods are specific 
 * to each EJB class. The method signatures must follow these rules: 
 * 
 * The method name must be ejbCreate. 
 *
 * The methods arguments must be legal types for RMI-IIOP. 
 * 
 */
public class EjbCreateMethodArgs extends EjbTest implements EjbCheck { 


    /** 
     * Session Bean's ejbCreate(...) methods arguments test.
     * Each session Bean class must define one or more ejbCreate(...) methods. 
     * The number and signatures of a session Bean's create methods are specific 
     * to each EJB class. The method signatures must follow these rules: 
     * 
     * The method name must be ejbCreate. 
     *
     * The methods arguments must be legal types for RMI-IIOP. 
     * 
     * @param descriptor the Enterprise Java Bean deployment descriptor
     *   
     * @return <code>Result</code> the results for this assertion
     */
    public Result check(EjbDescriptor descriptor) {

	Result result = getInitializedResult();
	ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();
	boolean remote_exists = false;

	if (descriptor instanceof EjbSessionDescriptor) {
	    boolean oneFailed = false;
	    try {
		VerifierTestContext context = getVerifierContext();
		ClassLoader jcl = context.getClassLoader();
		Class c = Class.forName(descriptor.getEjbClassName(), false, getVerifierContext().getClassLoader());

		Class [] ejbCreateMethodParameterTypes;
		boolean ejbCreateFound = false;
		boolean isLegalRMIIIOP = false;
	        int foundAtLeastOne = 0;

		//bug fix for 4699227 
                if (descriptor.getHomeClassName() == null ||
                        descriptor.getHomeClassName().equals("")) {
                       result.addNaDetails(smh.getLocalString
                        ("tests.componentNameConstructor", "For [ {0} ]",
                         new Object[] {compName.toString()}));
                       result.notApplicable(smh.getLocalString
                       ("com.sun.enterprise.tools.verifier.tests.ejb.localinterfaceonly.notapp",
                        "Not Applicable because, EJB [ {0} ] has Local Interfaces only.",
                                          new Object[] {descriptor.getEjbClassName()}));

                       return result;
                }

		if (descriptor.getRemoteClassName() != null &&
		    !descriptor.getRemoteClassName().equals(""))
		    remote_exists = true;

                // start do while loop here....
                do {
		    Method [] methods = c.getDeclaredMethods();
		    for (int i = 0; i < methods.length; i++) {
			// reset flags from last time thru loop
			ejbCreateFound = false;
			isLegalRMIIIOP = false;
			
			// The method name must be ejbCreate. 
			if (methods[i].getName().startsWith("ejbCreate")) {
			    foundAtLeastOne++;
			    ejbCreateFound = true;
			    			    
			    if (remote_exists == true) {
				// The methods arguments types must be legal types for RMI-IIOP.
				ejbCreateMethodParameterTypes = methods[i].getParameterTypes();
				if (RmiIIOPUtils.isValidRmiIIOPParameters(ejbCreateMethodParameterTypes)) {
				    // these method parameters are valid, continue
				    isLegalRMIIIOP = true;
				}
			    } else isLegalRMIIIOP = true; //if the ejb has only a local interface
			                                  //we donot need to check for legal rmi-iiop parameters
			    // now display the appropriate results for this particular ejbCreate

			    // now display the appropriate results for this particular ejbCreate
			    // method
			    if (ejbCreateFound && isLegalRMIIIOP) {
				result.addGoodDetails(smh.getLocalString
						      ("tests.componentNameConstructor",
						       "For [ {0} ]",
						       new Object[] {compName.toString()}));
				result.addGoodDetails(smh.getLocalString
						      (getClass().getName() + ".debug1",
						       "For EJB Class [ {0} ] method [ {1} ]",
						       new Object[] {descriptor.getEjbClassName(),methods[i].getName()}));
				result.addGoodDetails(smh.getLocalString
						      (getClass().getName() + ".passed",
						       "[ {0} ] properly declares [ {1} ] method with valid RMI-IIOP argument types.",
						       new Object[] {descriptor.getEjbClassName(),methods[i].getName()}));
			    } else if (ejbCreateFound && (!isLegalRMIIIOP)) {
				oneFailed = true;
				result.addErrorDetails(smh.getLocalString
						       ("tests.componentNameConstructor",
							"For [ {0} ]",
							new Object[] {compName.toString()}));
				result.addErrorDetails(smh.getLocalString
						       (getClass().getName() + ".debug1",
							"For EJB Class [ {0} ] method [ {1} ]",
							new Object[] {descriptor.getEjbClassName(),methods[i].getName()}));
				result.addErrorDetails(smh.getLocalString
						       (getClass().getName() + ".failed",
							"Error: An [ {0} ] method was found, but [ {1} ] method has illegal parameter values.   [ {2} ] methods arguments types must be legal types for RMI-IIOP.",
							new Object[] {methods[i].getName(),methods[i].getName(),methods[i].getName()}));
			    } 
			}
		    }
                } while (((c = c.getSuperclass()) != null) && (foundAtLeastOne == 0));
        
		if (foundAtLeastOne == 0){
		    result.addErrorDetails(smh.getLocalString
					   ("tests.componentNameConstructor",
					    "For [ {0} ]",
					    new Object[] {compName.toString()}));
		    result.failed(smh.getLocalString
				  (getClass().getName() + ".failed1",
				   "Error: [ {0} ] does not properly declare at least one ejbCreate(...) method.  [ {1} ] is not a valid bean.",
				   new Object[] {descriptor.getEjbClassName(),descriptor.getEjbClassName()}));
		    oneFailed = true;
		}
	    } catch (ClassNotFoundException e) {
		Verifier.debug(e);
		result.addErrorDetails(smh.getLocalString
				       ("tests.componentNameConstructor",
					"For [ {0} ]",
					new Object[] {compName.toString()}));
		result.failed(smh.getLocalString
			      (getClass().getName() + ".failedException",
			       "Error: [ {0} ] class not found.",
			       new Object[] {descriptor.getEjbClassName()}));
		oneFailed = true;
	    }  

	    if (oneFailed) {
		result.setStatus(result.FAILED);
	    } else {
		result.setStatus(result.PASSED);
	    }

	    return result;
 
	} else {
	    result.addNaDetails(smh.getLocalString
				("tests.componentNameConstructor",
				 "For [ {0} ]",
				 new Object[] {compName.toString()}));
	    result.notApplicable(smh.getLocalString
				 (getClass().getName() + ".notApplicable",
				  "[ {0} ] expected {1} bean, but called with {2} bean.",
				  new Object[] {getClass(),"Session","Entity"}));
	    return result;
	}
    }
}
