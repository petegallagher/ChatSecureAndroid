/*
 * Copyright (C) 2007-2008 Esmertec AG. Copyright (C) 2007-2008 The Android Open
 * Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package info.guardianproject.otr.app.im.app;

import info.guardianproject.otr.app.im.R;
import info.guardianproject.otr.app.im.provider.Imps;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.database.DatabaseUtils;


/** Activity used to pick a contact. */
public class ContactsPickerActivity extends ListActivity {
    public final static String EXTRA_EXCLUDED_CONTACTS = "excludes";

    public final static String EXTRA_RESULT_USERNAME = "result";
    public final static String EXTRA_RESULT_PROVIDER = "provider";
    public final static String EXTRA_RESULT_ACCOUNT = "account";    

    private ContactAdapter mAdapter;
    private String mExcludeClause;
    Uri mData;
    
    private EditText mFilterInput;
    private String mSearchString;
    
    private int mLoaderId = 1;

    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.contacts_picker_activity);

        mFilterInput = (EditText) findViewById(R.id.filter);
        mFilterInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                
                mSearchString = mFilterInput.getText().toString();
                doFilter(mSearchString);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              
                
            }
        });
        
        doFilter("");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = (Cursor) mAdapter.getItem(position);
        Intent data = new Intent();
        data.putExtra(EXTRA_RESULT_USERNAME, cursor.getString(ContactView.COLUMN_CONTACT_USERNAME));
        data.putExtra(EXTRA_RESULT_PROVIDER, cursor.getLong(ContactView.COLUMN_CONTACT_PROVIDER));
        data.putExtra(EXTRA_RESULT_ACCOUNT, cursor.getLong(ContactView.COLUMN_CONTACT_ACCOUNT));
        
        setResult(RESULT_OK, data);
        this.stopManagingCursor(cursor);
        finish();
    }
    
    public void doFilter(String filterString) {
        mSearchString = filterString;
        if (mAdapter == null) {
            
                mAdapter = new ContactAdapter(ContactsPickerActivity.this, R.layout.contact_view);

                setListAdapter(mAdapter);
            

            //mLoaderCallbacks = new MyLoaderCallbacks();
            //mLoaderManager.initLoader(mLoaderId, null, mLoaderCallbacks);
        } else {
            //mLoaderManager.restartLoader(mLoaderId, null, mLoaderCallbacks);
        }
        
        StringBuilder buf = new StringBuilder();

        if (mSearchString != null) {
            
            buf.append(Imps.Contacts.NICKNAME);
            buf.append(" LIKE ");
            DatabaseUtils.appendValueToSql(buf, "%" + mSearchString + "%");
            buf.append(" OR ");
            buf.append(Imps.Contacts.USERNAME);
            buf.append(" LIKE ");
            DatabaseUtils.appendValueToSql(buf, "%" + mSearchString + "%");
            
        }
        
        Cursor c = managedQuery(Imps.Contacts.CONTENT_URI_CONTACTS_BY, ContactView.CONTACT_PROJECTION,
                    buf == null ? null : buf.toString(), null, Imps.Contacts.DEFAULT_SORT_ORDER);
        
        mAdapter.swapCursor(c);
        
    }
    
    private class ContactAdapter extends ResourceCursorAdapter {
        
        
        public ContactAdapter(Context context, int view) {
            super(context, view, null);
            
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            
            View view = super.newView(context, cursor, parent);
          
            ContactView.ViewHolder holder = null;
                
            holder = new ContactView.ViewHolder();
                
            holder.mLine1 = (TextView) view.findViewById(R.id.contactStatus);
            holder.mLine2 = (TextView) view.findViewById(R.id.line2);
               
            holder.mTimeStamp = (TextView) view.findViewById(R.id.timestamp);
            holder.mAvatar = (ImageView)view.findViewById(R.id.contactAvatar);
                
            holder.mStatusBlock = view.findViewById(R.id.status_block);
                
            view.setTag(holder);
            
           return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ContactView v = (ContactView) view;
            v.bind(cursor, mSearchString, true);
            
        }
    }


   
}
