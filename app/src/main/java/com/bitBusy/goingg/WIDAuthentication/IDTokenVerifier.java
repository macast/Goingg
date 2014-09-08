/**
 * 
 */
package com.bitBusy.goingg.WIDAuthentication;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import com.bitBusy.goingg.dialog.WIDAuthenticationDialog;
import com.bitBusy.goingg.utility.Utility;

/**
 * @author SumaHarsha
 *
 */
public class IDTokenVerifier 
{
	private static final String ANDROIDCID = "58548578962-a0qga4f05fmetqf1htjd6k1f2nss06mc.apps.googleusercontent.com";
	private static final String TOKENISSUERID = "accounts.google.com";
	private static final String TOKENSPLITTER = ".";
	private static final String DECODEERROR = "Token decode error";
	private static final String CERTID = "kid";
	private static final String CERTALGO = "alg";
	private static final String STDCERTALGO = "RS256";
	private static final String SIGNATUREALGO = "SHA256withRSA";
	private static final String CERTIFICATEFACTORY = "X509";
	private static final String BYTECHARSET = "UTF-8";
	private enum PAYLOADVALS {aud, azp, iss, exp, email, email_verified};
	private Context myContext;
	private String myIDToken;
	private String myFirstPartToken;
	private String mySecondPartToken;
	private String myThirdPartToken;
	private String myTokenCertAlgo;
	private String myTokenCertID;
	private ArrayList<PublicKey> myPublicKeys;
	private boolean myTokenVerified = false;
	private IDTokenVerificationObserver myObserver;
	private long myTokenExpiration;
	private String myEmail;
	
	public IDTokenVerifier(Context the_context, String the_token)
	{
		myContext = the_context;
		myIDToken = the_token;
	}
	public boolean verify()
	{
		if (containsThreeParts())
		{
			new GglCertificateRetriever().execute();
		}
		return true;
	}
	
	public void registerObserver(IDTokenVerificationObserver the_observer){
		myObserver = the_observer;
	}
	
	public void notifyObserver(){
		if (myObserver != null){
			myObserver.onIDTokenVerified(myTokenVerified,
					myTokenVerified?new WIDAccessToken(myIDToken, myTokenExpiration, myEmail):null);
		}
	}
	private boolean containsThreeParts() 
	{
		if (myIDToken != null)
		{
			String[] components = myIDToken.split("\\" + TOKENSPLITTER);
			if (components != null && components.length == 3)
			{
				myFirstPartToken = components[0];
				mySecondPartToken = components[1];
				myThirdPartToken = components[2];
				return true;
			}
		}
		return false;
	}

	public void initializePublicKeys(String the_jsonCerts) 
	{
		if (the_jsonCerts != null)
		{
			JSONObject certs;
			try 
			{
				certs = new JSONObject(the_jsonCerts);
				if (certs != null)
				{
					JSONArray ids = 
							certs.names();
					if (ids != null)
					{
						initializePublicKeysFromJson(ids, certs);
					}
				}
			}
			catch (JSONException e) 
			{
				Utility.throwErrorMessage(myContext, DECODEERROR, e.getMessage());
			}
		}		
	}
	
	private void initializePublicKeysFromJson(JSONArray the_names, JSONObject the_jsonCerts)
	{
		if (the_names != null && the_jsonCerts != null)
		{
			String temp;
		    CertificateFactory factory;
		    myPublicKeys = new ArrayList<PublicKey>();
			try 
			{
				factory = CertificateFactory.getInstance(CERTIFICATEFACTORY);
				for (int i = 0; i < the_names.length(); i++)
				{
					try 
					{
						temp = the_names.getString(i);
						if (temp != null && the_jsonCerts.getString(temp) != null)
						{
							X509Certificate x509Cert = null;
							x509Cert = (X509Certificate) factory.generateCertificate(
										new ByteArrayInputStream(
												the_jsonCerts.getString(temp).getBytes(BYTECHARSET)));
								if (x509Cert != null)
								{
									myPublicKeys.add(x509Cert.getPublicKey());
								}
						}
					} 
					catch (JSONException e) 
					{
					}
				}
			}
		 catch (Exception e) 
		 {
			 Utility.throwErrorMessage(myContext, DECODEERROR, e.getMessage());
		 }	          
		}
	}

	public void onGglPublicCertsObtained() 
	{
		getTokenCertInfo();
		myTokenVerified = verifyTokenCert() && verifyPayloadFields();
		notifyObserver();
	}

	private boolean verifyPayloadFields() {
		String secondPartDecoded = decodeBase64Android(mySecondPartToken);
		JSONObject secondPartJson = getJsonObject(secondPartDecoded);
		boolean result = verifyAud(secondPartJson) && verifyAzp(secondPartJson) && verifyIssuer(secondPartJson);
		myTokenExpiration = result?initializeTokenExpirationTime(secondPartJson):0;
		myEmail = result?initializeEmail(secondPartJson):null;
		return result;
	}
	
	private String initializeEmail(JSONObject the_secondPartJson) {
		if (the_secondPartJson != null && isEmailVerified(the_secondPartJson)){
			return readPayloadValue(the_secondPartJson,PAYLOADVALS.email);
		}
		return null;
	}
	private boolean isEmailVerified(JSONObject the_secondPartJson) {
		if (the_secondPartJson != null){
			try {
				return the_secondPartJson.getBoolean(PAYLOADVALS.email_verified.name());
			} catch (JSONException e) {
				Utility.throwErrorMessage(myContext, DECODEERROR, e.getMessage());
			}
		}
		return false;
	}
	private long initializeTokenExpirationTime(JSONObject the_secondPartJson) {
		if (the_secondPartJson != null){
			String exp = readPayloadValue(the_secondPartJson, PAYLOADVALS.exp);
			try{
				return Long.parseLong(exp);
			}catch(Exception e){
				Utility.throwErrorMessage(myContext, DECODEERROR, e.getMessage());
				}
		}
		return 0;
	}
	private boolean verifyIssuer(JSONObject the_secondPartJson) {
		String issuer = readPayloadValue(the_secondPartJson, PAYLOADVALS.iss);
		return checkIssuerEquality(issuer);
	}
	
	private boolean checkIssuerEquality(String the_issuer) {
		if (the_issuer != null){
			return TOKENISSUERID.equals(the_issuer);
		}
		return false;
	}
	private boolean verifyAzp(JSONObject the_secondPartJson) {
		String azp = readPayloadValue(the_secondPartJson, PAYLOADVALS.azp);
		return checkAzpEquality(azp);
	}
	
	private boolean checkAzpEquality(String the_azpValue) {
		if (the_azpValue != null){
			return ANDROIDCID.equals(the_azpValue);
		}
		return false;		
	}
	
	private boolean verifyAud(JSONObject the_secondPartJson)
	{	
		String aud = readPayloadValue(the_secondPartJson, PAYLOADVALS.aud);
		return checkAudEquality(aud);
	}
	
	private boolean checkAudEquality(String the_audValue) {
		if (the_audValue != null){
			return WIDAuthenticationDialog.GCLIENTID.equals(the_audValue);
		}
		return false;
	}
	private String readPayloadValue(JSONObject the_json, PAYLOADVALS the_val) {
		if (the_json != null && the_val != null){
			try {
				return the_json.getString(the_val.name());
			} catch (JSONException e) {
				Utility.throwErrorMessage(myContext, DECODEERROR, e.getMessage());
			}
		}
		return null;
	}
	private JSONObject getJsonObject(String the_string) 
	{
		if (the_string != null){
			try {
				JSONObject jsonObj = new JSONObject(the_string);
				return jsonObj;
			} catch (JSONException e) {
				Utility.throwErrorMessage(myContext, DECODEERROR, the_string + " " + e.getMessage());
			}
		}
		return null;
	}
	private boolean verifyTokenCert() 
	{
		if (myTokenCertAlgo != null && myTokenCertID != null && myTokenCertAlgo.equalsIgnoreCase(STDCERTALGO) && myPublicKeys != null
				&& myFirstPartToken != null && mySecondPartToken != null && myThirdPartToken != null)
		{
		    Signature signer;
			try 
			{
				signer = Signature.getInstance(SIGNATUREALGO);
	        	byte[] decodedSign = decodeBase64(myThirdPartToken);	
				for (PublicKey publicKey : myPublicKeys) 
				{
					signer.initVerify(publicKey);
			        signer.update((myFirstPartToken.concat(TOKENSPLITTER).concat(mySecondPartToken)).getBytes(BYTECHARSET));
		       	   if (signer.verify(decodedSign))
				   {
					   return true;
				   }
				}
			}
			catch(Exception e)
			{
				Utility.throwErrorMessage(myContext, DECODEERROR, e.getMessage());
			}
		}
		return false;
	}
	
	private void getTokenCertInfo() 
	{
		if (myFirstPartToken != null)
		{
			String decodedFirstPart = decodeBase64Android(myFirstPartToken);
			getCertAlgoAndID(decodedFirstPart);
		}
	}

	private void getCertAlgoAndID(String the_string) 
	{
		if (the_string != null)
		{
			try 
			{
				JSONObject certJSON = new JSONObject(the_string);
				if (certJSON != null)
				{
					myTokenCertAlgo = certJSON.getString(CERTALGO);
					myTokenCertID = certJSON.getString(CERTID);
				}
			}
			catch (JSONException e)
			{
				Utility.throwErrorMessage(myContext, DECODEERROR, e.getMessage());
			}
			
		}
	}
	
	private String decodeBase64Android(String the_string)
	{
		if (the_string != null)
		{
			byte[] data = Base64.decode(the_string, Base64.URL_SAFE);
			try {
				return new String(data, BYTECHARSET); 
			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	private byte[] decodeBase64(String the_string)
	{
		if (the_string != null)
		{
				try {
					return  Base64.decode(the_string.getBytes(BYTECHARSET), Base64.URL_SAFE);
				} catch (UnsupportedEncodingException e) {
					Utility.throwErrorMessage(myContext, DECODEERROR, e.getMessage());
				}
		}
		return null;
	}
	private class GglCertificateRetriever extends AsyncTask<Void, Void, Void>{
				
		private static final String VERIFIYINGSERVER = "Verifiying server identity...";

		private static final String VERIFIERURI = "https://www.googleapis.com/oauth2/v1/certs";

		private static final String GGLPBLICCERTERROR = "Cert retrieve error";
						
		private ProgressDialog myProgressDialog;

		@Override
	    protected void onPreExecute()
		{
			myProgressDialog = new ProgressDialog(myContext);
			myProgressDialog.setMessage(VERIFIYINGSERVER);
		    myProgressDialog.setCanceledOnTouchOutside(false);
			myProgressDialog.show();
		}
		
		@Override
	    protected void onPostExecute(Void params)
	    {
			closeDialog();
			onGglPublicCertsObtained();
	    }
		
		private void closeDialog() 
		{
			if (myProgressDialog != null && myProgressDialog.isShowing())
			{
				myProgressDialog.cancel();
			}
		}

		@Override
		protected Void doInBackground(Void... params) 
		{
			readToken();
			return null;
		}
		
		private void readToken()
		{
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(VERIFIERURI);
			StringBuilder dataBuilder = new StringBuilder();

			try {
				  HttpResponse response = client.execute(httpGet);
				  StatusLine statusLine = response.getStatusLine();
				  int statusCode = statusLine.getStatusCode();
				  if (statusCode == 200)
				  {
				    HttpEntity entity = response.getEntity();
				    InputStream content = entity.getContent();
				    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				    String line;
				    while ((line = reader.readLine()) != null) 
				    {
				    	dataBuilder.append(line);
				    }
					initializePublicKeys(dataBuilder.toString());
				  } 
				} catch (ClientProtocolException e)
				{
					Utility.throwErrorMessage(myContext, GGLPBLICCERTERROR, e.getMessage());
				} catch (IOException e)
				{
					Utility.throwErrorMessage(myContext, GGLPBLICCERTERROR, e.getMessage());
				}
		}
	}
}

