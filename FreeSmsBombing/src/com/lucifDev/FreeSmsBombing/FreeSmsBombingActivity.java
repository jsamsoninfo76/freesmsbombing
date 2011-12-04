package com.lucifDev.FreeSmsBombing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class FreeSmsBombingActivity extends Activity {
	/* VARIABLE POUR L'INTERFACE GRAPHIQUE*/
	private Button contact;
	private EditText numero,cpt,message;
	//test
	/* VARIABLE POUR LA RECUPERATION DES CONTACTS */
	private static final int CONTACT_PICKER_RESULT = 1001;
	private static final String DEBUG_TAG = "InviteActivity";
	
	/* VARIABLE POUR LA PROGRESSBAR */
	enum ErrorStatus {
	    NO_ERROR, ERROR_1, ERROR_2
	};
	protected ProgressDialog mProgressDialog;
	private ErrorStatus status;
	public static final int MSG_ERR = 0;
	public static final int MSG_CNF = 1;
	public static final int MSG_IND = 2;
	public static final String TAG = "ProgressBarActivity";
	
	//private Layout 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //On récupère le bouton créer en XML grâce à son id
        Button btnEnvoie = (Button)findViewById(R.id.envoyer);
        contact = (Button)findViewById(R.id.button1);
    	numero =(EditText)findViewById(R.id.numero);
        cpt = (EditText)findViewById(R.id.cpt);
        message = (EditText)findViewById(R.id.message);
        
     // Look up the AdView as a resource and load a request.
        AdView adView = (AdView)this.findViewById(R.id.adView);
        adView.loadAd(new AdRequest());
        
        //Ajout du premier Listener sur le boutton contact pour les afficher
        contact.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				 Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,Contacts.CONTENT_URI);
				 startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
			}});
        
        //On affecte un écouteur d'évènement au bouton
        btnEnvoie.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					initLoader();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    }
	
	/* MENU */
    public boolean onCreateOptionsMenu(Menu menu) {
        //Création d'un MenuInflater qui va permettre d'instancier un Menu XML en un objet Menu
        MenuInflater inflater = getMenuInflater();
        //Instanciation du menu XML spécifier en un objet Menu
        inflater.inflate(R.layout.menu, menu); 
        return true;
     }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
          case R.id.quitter:
              finish();
              return true;
          case R.id.partager:
        	  final Intent MessIntent = new Intent(Intent.ACTION_SEND);
        	  MessIntent.setType("text/plain");
	       	  MessIntent.putExtra(Intent.EXTRA_TEXT, "https://market.android.com/details?id=com.lucifDev.FreeSmsBombing");
	       	  FreeSmsBombingActivity.this.startActivity(Intent.createChooser(MessIntent, getString(R.string.partager)));
	          return true;
        }
        return false;
     }
	
	public void initLoader() throws InterruptedException{
		sendSms();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		 if (resultCode == RESULT_OK) {
		        switch (requestCode) {
		        case CONTACT_PICKER_RESULT:
		            Cursor cursor = null;
		            String num = "";
		            try {
		                Uri result = data.getData();
		                Log.v(DEBUG_TAG, "Got a contact result: "
		                        + result.toString());
		 
		                // get the contact id from the Uri
		                String id = result.getLastPathSegment();
		 
		                // query for everything email
		                cursor = getContentResolver().query(Phone.CONTENT_URI,
		                        null, Phone.CONTACT_ID + "=?", new String[] { id },
		                        null);
		 
		                int numIdx = cursor.getColumnIndex(Phone.DATA);
		 
		                // let's just get the first number
		                if (cursor.moveToFirst()) {
		                	num = cursor.getString(numIdx);
		                    Log.v(DEBUG_TAG, "Got num: " + num);
		                } else {
		                    Log.w(DEBUG_TAG, "No results");
		                }
		            } catch (Exception e) {
		                Log.e(DEBUG_TAG, getString(R.string.erreurContact_recup_num), e);
		            } finally {
		                if (cursor != null) {
		                    cursor.close();
		                }
		                
		                numero.setText(num);
		                if (num.length() == 0) {
		                    Toast.makeText(this, getString(R.string.erreurContact_no_num),
		                            Toast.LENGTH_LONG).show();
		                }
		            }
		            break;
		        }
	        } else {
	            // gracefully handle failure
	           Log.w(DEBUG_TAG, getString(R.string.erreurContact_button));
	        }
	}
	 
	/** fake operation for testing purpose 
	 * @throws InterruptedException */
	protected ErrorStatus sendSms() throws InterruptedException {
	    	//On récupère ce qui a été entré dans les EditText
			final String num = numero.getText().toString();
			final String msg = message.getText().toString();
			int nbMsg = 0;
			
			try{
				nbMsg = Integer.parseInt(cpt.getText().toString());
				
				//Limitation de la version gratuite=5
				if (nbMsg > 5)
				{
					Toast.makeText(FreeSmsBombingActivity.this, "Vous êtes limité à 5 textos.", Toast.LENGTH_SHORT).show();
					nbMsg = 5;
				}
				
			}catch(NumberFormatException e){
				Toast.makeText(FreeSmsBombingActivity.this, getString(R.string.erreurVerif_nbmsg), Toast.LENGTH_SHORT).show();
			}

			// On lance la procédure d'envoi
			if(num.length()>= 4 && msg.length() > 0)
			{
				//Grâce à l'objet de gestion de SMS (SmsManager) que l'on récupère grâce à la méthode static getDefault()
				//On envoit le SMS à l'aide de la méthode sendTextMessage
				for (int i=0 ; i<nbMsg ; i++)
				{
					Thread.sleep(1000);
					SmsManager.getDefault().sendTextMessage(num, null, msg, null, null);
				}
				
				Toast.makeText(FreeSmsBombingActivity.this, getString(R.string.msg_send) + nbMsg + " fois.", Toast.LENGTH_SHORT).show();
				
				//On efface les deux EditText
				numero.setText("");
				message.setText("");
				cpt.setText("");
			}else{
				//On affiche un petit message d'erreur dans un Toast
				Toast.makeText(FreeSmsBombingActivity.this, getString(R.string.erreurVerif_num), Toast.LENGTH_SHORT).show();
			}
			return status;
	}	
}