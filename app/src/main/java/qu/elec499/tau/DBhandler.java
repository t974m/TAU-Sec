package qu.elec499.tau;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBhandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "residents.db";
    private static final String TABLE_RESIDENTS = "residenttable";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_NP = "NP";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_PHOTO = "photo";
    private static final String COLUMN_RESUME = "resume";

    DBhandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query =
                "CREATE TABLE " + TABLE_RESIDENTS + " ("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_NAME + " VARCHAR(30), "
                        + COLUMN_PHONE + " VARCHAR(10), "
                        + COLUMN_NP + " VARCHAR(30), "
                        + COLUMN_STATUS + " VARCHAR(15), "
                        + COLUMN_PHOTO + " VARCHAR(50), "
                        + COLUMN_RESUME + " VARCHAR(50));";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RESIDENTS);
        onCreate(sqLiteDatabase);
    }

    void addResident(Resident resident) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, resident.getName());
        contentValues.put(COLUMN_PHONE, resident.getPhone());
        contentValues.put(COLUMN_NP, resident.getNP());
        contentValues.put(COLUMN_STATUS, resident.getStatus());
        contentValues.put(COLUMN_PHOTO, resident.getPhotoURI());
        contentValues.put(COLUMN_RESUME, resident.getResumeURI());
        sqLiteDatabase.insert(TABLE_RESIDENTS, null, contentValues);
        sqLiteDatabase.close();
    }

    Resident returnResident(int resident_id) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_RESIDENTS
                + " WHERE " + COLUMN_ID + " = " + resident_id;

        Resident resident = new Resident();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            resident.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            resident.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
            resident.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
            resident.setNP(cursor.getString(cursor.getColumnIndex(COLUMN_NP)));
            resident.setPhotoURI(cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO)));
            resident.setResumeURI(cursor.getString(cursor.getColumnIndex(COLUMN_RESUME)));
            cursor.close();
        }
        return resident;
    }

    ArrayList<Resident> returnResidents(int number) {

        String query = "";
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        switch (number) {
            case 0:
                query = "SELECT * FROM " + TABLE_RESIDENTS + ";";
                break;
            case 1:
                query = "SELECT * FROM " + TABLE_RESIDENTS
                        + " WHERE " + COLUMN_STATUS + " = 'Active'";
                break;
            case 100:
                query = "SELECT * FROM " + TABLE_RESIDENTS
                        + " ORDER BY " + COLUMN_STATUS + ";";
                break;
            case 200:
                query = "SELECT * FROM " + TABLE_RESIDENTS
                        + " ORDER BY " + COLUMN_NAME + ";";
                break;
            case 201:
                query = "SELECT * FROM " + TABLE_RESIDENTS
                        + " WHERE " + COLUMN_STATUS + " = 'Active' "
                        + " ORDER BY " + COLUMN_NAME + ";";
                break;
            case 300:
                query = "SELECT * FROM " + TABLE_RESIDENTS
                        + " ORDER BY " + COLUMN_NP + ";";
                break;
            case 301:
                query = "SELECT * FROM " + TABLE_RESIDENTS
                        + " WHERE " + COLUMN_STATUS + " = 'Active' "
                        + " ORDER BY " + COLUMN_NP + ";";
                break;
        }

        ArrayList<Resident> residentArrayList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Resident resident = new Resident();
                resident.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                resident.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                resident.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                resident.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
                resident.setNP(cursor.getString(cursor.getColumnIndex(COLUMN_NP)));
                resident.setPhotoURI(cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO)));
                resident.setResumeURI(cursor.getString(cursor.getColumnIndex(COLUMN_RESUME)));
                residentArrayList.add(resident);
                cursor.moveToNext();
            }
            cursor.close();
        }

        sqLiteDatabase.close();
        return residentArrayList;
    }

    void updateCandidate(int id, String name, String phone, String position, String status, String photoURI, String resumeUri) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "UPDATE " + TABLE_RESIDENTS + " SET "
                + COLUMN_NAME + " = '" + name + "', "
                + COLUMN_PHONE + " = '" + phone + "', "
                + COLUMN_NP + " = '" + position + "', "
                + COLUMN_STATUS + " = '" + status + "', "
                + COLUMN_PHOTO + " = '" + photoURI + "',"
                + COLUMN_RESUME + " = '" + resumeUri + "'"
                + " WHERE " + COLUMN_ID + " = " + id;
        sqLiteDatabase.execSQL(query);
    }

    void deleteAll() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_RESIDENTS, null, null);
    }

    void deleteSelected() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_RESIDENTS,COLUMN_STATUS + " = 'DELETE' ;",null);
    }

    ArrayList<Resident> returnQuery(String search) {
        String query = "SELECT * FROM " + TABLE_RESIDENTS
                + " WHERE " + COLUMN_NAME + " LIKE '%" + search.toLowerCase() + "%';";
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        ArrayList<Resident> residentArrayList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Resident resident = new Resident();
                resident.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                resident.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                resident.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                resident.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
                resident.setNP(cursor.getString(cursor.getColumnIndex(COLUMN_NP)));
                resident.setPhotoURI(cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO)));
                resident.setResumeURI(cursor.getString(cursor.getColumnIndex(COLUMN_RESUME)));
                residentArrayList.add(resident);
                cursor.moveToNext();
            }
            cursor.close();
        }

        sqLiteDatabase.close();
        return residentArrayList;
    }
}
