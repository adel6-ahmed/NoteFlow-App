package com.example.taskreminderapp.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.taskreminderapp.User;
import com.example.taskreminderapp.models.Task;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "task_reminder.db";
    private static final int DATABASE_VERSION = 3;

    // جداول قاعدة البيانات
    private static final String TABLE_TASKS = "tasks";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SETTINGS = "settings";

    // أعمدة جدول المهام
    private static final String COLUMN_TASK_ID = "id";
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_DESCRIPTION = "description";
    private static final String COLUMN_TASK_CATEGORY = "category";
    private static final String COLUMN_TASK_DUE_DATE = "due_date";
    private static final String COLUMN_USER_ID = "user_id";

    // أعمدة جدول المستخدمين
    private static final String COLUMN_USER_ID_USERS = "id";
    private static final String COLUMN_USER_USERNAME = "username";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_NAME = "full_name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PHONE = "phone";

    // أعمدة جدول الإعدادات
    private static final String COLUMN_SETTINGS_ID = "id";
    private static final String COLUMN_SETTINGS_USER_ID = "user_id";
    private static final String COLUMN_SETTINGS_NOTIFICATIONS = "notifications_enabled";
    private static final String COLUMN_SETTINGS_THEME = "theme";
    private static final String COLUMN_SETTINGS_LANGUAGE = "language";

    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // إنشاء جدول المهام
        String createTaskTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK_TITLE + " TEXT, " +
                COLUMN_TASK_DESCRIPTION + " TEXT, " +
                COLUMN_TASK_CATEGORY + " TEXT, " +
                COLUMN_TASK_DUE_DATE + " TEXT, " +
                COLUMN_USER_ID + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID_USERS + ")" +
                ")";
        db.execSQL(createTaskTable);

        // إنشاء جدول المستخدمين
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID_USERS + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_USERNAME + " TEXT UNIQUE, " +
                COLUMN_USER_PASSWORD + " TEXT, " +
                COLUMN_USER_NAME + " TEXT, " +
                COLUMN_USER_EMAIL + " TEXT, " +
                COLUMN_USER_PHONE + " TEXT" +
                ")";
        db.execSQL(createUserTable);

        // إنشاء جدول الإعدادات
        String createSettingsTable = "CREATE TABLE " + TABLE_SETTINGS + " (" +
                COLUMN_SETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SETTINGS_USER_ID + " INTEGER, " +
                COLUMN_SETTINGS_NOTIFICATIONS + " INTEGER DEFAULT 1, " +
                COLUMN_SETTINGS_THEME + " TEXT DEFAULT 'light', " +
                COLUMN_SETTINGS_LANGUAGE + " TEXT DEFAULT 'en', " +
                "FOREIGN KEY (" + COLUMN_SETTINGS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID_USERS + ")" +
                ")";
        db.execSQL(createSettingsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
            onCreate(db);
        }
    }

    // حفظ userId في SharedPreferences
    private void saveUserId(long userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("userId", userId);
        editor.apply();
    }

    // دالة لتسجيل مستخدم جديد
    public boolean registerUser(String username, String password, String email, String phone, String fullName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PHONE, phone);
        values.put(COLUMN_USER_NAME, fullName);

        long userId = db.insert(TABLE_USERS, null, values);
        db.close();

        return userId != -1;  // إذا كانت القيمة غير -1 فإن المستخدم تم إضافته بنجاح
    }


    // التحقق من تسجيل الدخول وإرجاع userId
    @SuppressLint("Range")
    public long loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        long userId = -1;

        String query = "SELECT " + COLUMN_USER_ID_USERS + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USER_USERNAME + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{username, password})) {
            if (cursor.moveToFirst()) {
                userId = cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID_USERS));
                saveUserId(userId);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Login error: " + e.getMessage());
        }

        return userId;
    }

    // استرجاع جميع المهام الخاصة بمستخدم
    @SuppressLint("Range")

    public List<Task> getAllTasks(long userId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<Task> taskList = new ArrayList<>();

        try {
            db = this.getReadableDatabase();

            // استخدام استعلام SQL مع تصفية حسب userId
            String query = "SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long taskId = cursor.getLong(cursor.getColumnIndex(COLUMN_TASK_ID));  // استخدام getLong بدلاً من getInt
                    String title = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_TITLE));
                    String description = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_DESCRIPTION));
                    String category = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_CATEGORY));
                    String dueDate = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_DUE_DATE));

                    // إذا كان هناك عمود "userId" مرتبط بالمهام
                    userId = cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID));

                    Task task = new Task(taskId, title, description, category, dueDate);  // تمرير userId هنا إذا كان مطلوبًا
                    taskList.add(task);
                } while (cursor.moveToNext());
            }


        } catch (Exception e) {
            // يمكن إضافة سجل خطأ هنا إذا لزم الأمر
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // إغلاق الـ cursor
            }
            if (db != null) {
                db.close(); // إغلاق قاعدة البيانات
            }
        }

        return taskList;
    }



    // جلب بيانات المستخدم بواسطة userId
    @SuppressLint("Range")
    public User getUserById(long userId) {  // تغيير نوع المعامل إلى long
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID_USERS + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) {
                // إنشاء كائن User باستخدام القيم المسترجعة من قاعدة البيانات
                user = new User(
                        cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE))
                );
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching user by ID: " + e.getMessage());
        }

        return user;
    }


    public boolean deleteTask(long taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_TASKS, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
        return rowsDeleted > 0; // يعيد true إذا تم حذف صف واحد أو أكثر
    }


    // دالة تحديث مهمة
    public boolean updateTask(Task updatedTask) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, updatedTask.getTitle());
        values.put(COLUMN_TASK_DESCRIPTION, updatedTask.getDescription());
        values.put(COLUMN_TASK_CATEGORY, updatedTask.getCategory());
        values.put(COLUMN_TASK_DUE_DATE, updatedTask.getDueDate());

        db.update(TABLE_TASKS, values, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(updatedTask.getId())});
        db.close();
        return false;
    }

    // دالة جلب مهمة باستخدام taskId
    public Task getTaskById(long taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Task task = null;

        try {
            // تنفيذ الاستعلام لجلب المهمة بناءً على معرفها
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});

            // التحقق مما إذا كان هناك بيانات
            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(COLUMN_TASK_ID)); // الحصول على المعرف
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_TITLE)); // الحصول على العنوان
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_DESCRIPTION)); // الحصول على الوصف
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_CATEGORY)); // الحصول على التصنيف
                @SuppressLint("Range") String dueDate = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_DUE_DATE)); // الحصول على تاريخ الاستحقاق

                // إنشاء كائن المهمة بناءً على البيانات
                task = new Task(id, title, description, category, dueDate);
            } else {
                Log.d("DatabaseHelper", "No task found with ID: " + taskId);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while fetching task: " + e.getMessage());
        } finally {
            // إغلاق المؤشر إذا كان مفتوحًا
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return task; // إرجاع المهمة أو null إذا لم يتم العثور عليها
    }






    // التحقق من صحة بيانات تسجيل الدخول
    @SuppressLint("Range")
    public long checkLoginCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        long userId = -1;

        String query = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{username, password})) {
            if (cursor.moveToFirst()) {
                userId = cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID_USERS));
            }
        } catch (Exception e) {
            Log.e("LoginError", "Error in query: " + e.getMessage());
        }

        return userId;
    }


    public long insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase(); // فتح قاعدة البيانات للكتابة
        ContentValues values = new ContentValues();

        // إضافة القيم إلى ContentValues
        values.put(COLUMN_TASK_TITLE, task.getTitle());
        values.put(COLUMN_TASK_DESCRIPTION, task.getDescription());
        values.put(COLUMN_TASK_CATEGORY, task.getCategory());
        values.put(COLUMN_TASK_DUE_DATE, task.getDueDate());
        values.put(COLUMN_USER_ID, task.getUserId()); // التأكد من أن كل مهمة ترتبط بالمستخدم

        // إدخال البيانات في جدول المهام وإرجاع الـ ID الذي تم إنشاؤه
        long result = db.insert(TABLE_TASKS, null, values);

        // التحقق مما إذا كانت عملية الإدخال ناجحة
        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to insert task");
        } else {
            Log.d("DatabaseHelper", "Task inserted successfully with ID: " + result);
        }

        db.close(); // إغلاق قاعدة البيانات بعد العملية
        return result;  // إرجاع الـ ID الذي تم توليده
    }

    public int getTaskCount() {
        SQLiteDatabase db = this.getReadableDatabase(); // الحصول على قاعدة البيانات للقراءة
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM tasks", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0); // الحصول على العدد
        }
        cursor.close();
        db.close();
        return count;
    }


    public int getTaskCountForUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase(); // Open the database for reading
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM tasks WHERE user_id = ?", new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0); // Get the count from the first column
        }
        cursor.close(); // Close the cursor
        db.close(); // Close the database
        return count; // Return the task count
    }

    @SuppressLint("Range")
    public List<Task> searchTasks(int userId, String query) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();

            String sql = "SELECT * FROM tasks WHERE user_id = ? AND title LIKE ?";
            String[] selectionArgs = {String.valueOf(userId), "%" + query + "%"};

            cursor = db.rawQuery(sql, selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String description = cursor.getString(cursor.getColumnIndex("description"));

                    // قم بإنشاء كائن Task باستخدام منشئ مناسب
                    Task task = new Task(id, title, description, title, description);
                    tasks.add(task);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace(); // سجل الخطأ لتتبعه
        } finally {
            // تأكد من إغلاق الموارد بشكل آمن
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return tasks;
    }

}