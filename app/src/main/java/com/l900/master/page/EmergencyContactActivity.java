package com.l900.master.page;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.l900.master.R;
import com.l900.master.tools.DatabaseHelper;
import com.l900.master.tools.EmergencyContact;

import java.util.ArrayList;
import java.util.List;

public class EmergencyContactActivity extends Activity implements View.OnClickListener{

    private ListView list_emergency_contact;
    private TextView tv_no_one_emergency_contact;
    private DatabaseHelper mDatabaseHelper;
    private List<EmergencyContact> mData;
    private EmergencyContactAdapter mEmergencyContactAdapter;
    private ImageButton ib_emergency_contact_modify;
    private boolean delIdex = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);
        findViewById(R.id.btn_add_emergency_contact).setOnClickListener(this);
        list_emergency_contact = findViewById(R.id.list_emergency_contact);
        tv_no_one_emergency_contact = findViewById(R.id.tv_no_one_emergency_contact);
        findViewById(R.id.btn_emergency_contact_back).setOnClickListener(this);
        ib_emergency_contact_modify = findViewById(R.id.ib_emergency_contact_modify);
        ib_emergency_contact_modify.setOnClickListener(this);
        //init data and view
        mDatabaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        initData();
        mEmergencyContactAdapter = new EmergencyContactAdapter(mData,this.getApplicationContext());
        list_emergency_contact.setAdapter(mEmergencyContactAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exit();
    }

    private void exit() {
        try{
            if ((systemCursor!=null)&&(!systemCursor.isClosed())){
                systemCursor.close();
            }
            mDatabaseHelper.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initData() {
        Cursor cursor = mDatabaseHelper.query();
        if (mData==null){
            mData = new ArrayList<>();
        }else {
            mData.clear();
        }
        if (cursor.getCount() != 0){
            while(cursor.moveToNext()){
                String tempName = cursor.getString(cursor.getColumnIndex("name"));
                String tempTel = cursor.getString(cursor.getColumnIndex("tel"));
                Log.e("1900","initData  tel:"+ tempTel+" name :"+tempName);
                mData.add(new EmergencyContact(tempName,tempTel));
            }
        }
        if(mData.size()==0){
            tv_no_one_emergency_contact.setVisibility(View.VISIBLE);
            list_emergency_contact.setVisibility(View.GONE);
            delIdex = true;
            ib_emergency_contact_modify.setBackgroundResource(R.drawable.modify_back);
        }else{
            tv_no_one_emergency_contact.setVisibility(View.GONE);
            list_emergency_contact.setVisibility(View.VISIBLE);
        }
        Log.e("1900","mData size:"+mData.size());
    }


    private void addContact() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    private Cursor systemCursor;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1){
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                systemCursor = managedQuery(contactData, null, null, null,
                        null);
                systemCursor.moveToFirst();
                String tel = this.getContactPhone(systemCursor);
                Log.e("1900","pick tel:"+tel);
                Log.e("1900","pick displayName:"+getContactName(systemCursor));
                CanInsertContactToDB(tel,getContactName(systemCursor));
            }
        }
    }

    private void CanInsertContactToDB(String tel, String name) {
        Cursor cursor = mDatabaseHelper.query();
        boolean isExist = false;
        if (cursor.getCount() != 0){
            while(cursor.moveToNext()){
                String tempName = cursor.getString(cursor.getColumnIndex("name"));
                String tempTel = cursor.getString(cursor.getColumnIndex("tel"));
                if(name.equals(tempName) && tel.equals(tempTel)){
                    isExist = true;
                    break;
                }
            }
        }
        if (!isExist){
            insertContactToDB(name,tel);
        }
    }

    private void insertContactToDB(String name, String tel) {
        Log.d("1900", "insert "+name+"到紧急联系人库中 tel :"+tel);
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("tel", tel);
        mDatabaseHelper.insert( values);
        initData();
        mEmergencyContactAdapter.notifyDataSetChanged();
    }

    private String getContactPhone(Cursor cursor) {
        // TODO Auto-generated method stub
        int phoneColumn = cursor
                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String result = "";
        if (phoneNum > 0) {
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            Cursor phone = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                            + contactId, null, null);
            if (phone.moveToFirst()) {
                for (; !phone.isAfterLast(); phone.moveToNext()) {
                    int index = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    String phoneNumber = phone.getString(index);
                    result = phoneNumber;
                }
                if (!phone.isClosed()) {
                    phone.close();
                }
            }
        }
        return result;
    }

    private String getContactName(Cursor cursor) {
        int phoneColumn = cursor
                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String result = "";
        if (phoneNum > 0) {
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            Cursor phone = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                            + contactId, null, null);
            if (phone.moveToFirst()) {
                for (; !phone.isAfterLast(); phone.moveToNext()) {
                    int index = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    String phoneNumber = phone.getString(index);
                    result = phoneNumber;
                }
                if (!phone.isClosed()) {
                    phone.close();
                }
            }
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_emergency_contact_back:
                exit();
                EmergencyContactActivity.this.finish();
                break;
            case R.id.btn_add_emergency_contact:
                addContact();
                break;
            case R.id.ib_emergency_contact_modify:
                midifyClick();
                break;
        }
    }

    private void midifyClick() {
        if (mData.size()==0)
        {
            delIdex = true;
            ib_emergency_contact_modify.setBackgroundResource(R.drawable.modify_back);
            return;
        }
        if(delIdex){
            delIdex = false;
            ib_emergency_contact_modify.setBackgroundResource(R.drawable.finish_back);
        }else {
            delIdex = true;
            ib_emergency_contact_modify.setBackgroundResource(R.drawable.modify_back);
        }
        mEmergencyContactAdapter.notifyDataSetChanged();
    }

    class EmergencyContactAdapter extends BaseAdapter{

        private List<EmergencyContact> list;
        private Context mContext;

        public EmergencyContactAdapter(List<EmergencyContact> list, Context context) {
            this.list = list;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder ;
            if (convertView==null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.emergency_contact_list,parent,false);
                holder = new ViewHolder();

                holder.nameView = convertView.findViewById(R.id.name);
                holder.telView = convertView.findViewById(R.id.tel);
                holder.ib_del = convertView.findViewById(R.id.ib_del);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            EmergencyContact emergencyContact = list.get(position);
            holder.nameView.setText(emergencyContact.getName());
            holder.telView.setText(emergencyContact.getTel());
            if(delIdex){
                holder.ib_del.setVisibility(View.INVISIBLE);
            }else{
                holder.ib_del.setVisibility(View.VISIBLE);
            }
            holder.ib_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteContactInDB(position);
                }
            });
            return convertView;
        }

        class ViewHolder{
            TextView nameView;
            TextView telView;
            ImageButton ib_del;
        }
    }

    private void deleteContactInDB(int position) {
        EmergencyContact emergencyContact = mData.get(position);
        String name =emergencyContact.getName();
        String tel = emergencyContact.getTel();
        if (name!=null && name!=null){
            mDatabaseHelper.del(name,tel);
            initData();
            mEmergencyContactAdapter.notifyDataSetChanged();
        }

    }

}
