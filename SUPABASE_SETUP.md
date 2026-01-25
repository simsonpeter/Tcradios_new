# Supabase Setup Guide for TC RADIOS

## Step 1: Create Supabase Account

1. Go to https://app.supabase.com
2. Sign up for a free account (or log in if you already have one)
3. Click "New Project"
4. Fill in:
   - **Project Name**: tcradios (or any name you like)
   - **Database Password**: Create a strong password (save it!)
   - **Region**: Choose closest to your users
   - Click "Create new project"

## Step 2: Get Your API Credentials

1. Once your project is created, go to **Settings** (gear icon in sidebar)
2. Click **API** in the settings menu
3. You'll see:
   - **Project URL**: Copy this (looks like `https://xxxxx.supabase.co`)
   - **anon/public key**: Copy this (long string starting with `eyJ...`)

## Step 3: Update Your Code

1. Open `index.html`
2. Find these lines (around line 3462):
   ```javascript
   const SUPABASE_URL = 'YOUR_SUPABASE_URL';
   const SUPABASE_ANON_KEY = 'YOUR_SUPABASE_ANON_KEY';
   ```
3. Replace with your actual values:
   ```javascript
   const SUPABASE_URL = 'https://your-project-id.supabase.co';
   const SUPABASE_ANON_KEY = 'your-anon-key-here';
   ```

## Step 4: Create Database Tables

1. In Supabase dashboard, go to **SQL Editor** (in sidebar)
2. Click **New Query**
3. Copy and paste this SQL:

```sql
-- Create user_favorites table
CREATE TABLE IF NOT EXISTS user_favorites (
  id BIGSERIAL PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  favorites TEXT[] NOT NULL DEFAULT '{}',
  favorites_order TEXT[] NOT NULL DEFAULT '{}',
  updated_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(user_id)
);

-- Create user_ratings table
CREATE TABLE IF NOT EXISTS user_ratings (
  id BIGSERIAL PRIMARY KEY,
  user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
  station_name TEXT NOT NULL,
  rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(user_id, station_name)
);

-- Create index for faster queries
CREATE INDEX IF NOT EXISTS idx_user_favorites_user_id ON user_favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_user_ratings_station_name ON user_ratings(station_name);
CREATE INDEX IF NOT EXISTS idx_user_ratings_user_id ON user_ratings(user_id);

-- Enable Row Level Security (RLS)
ALTER TABLE user_favorites ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_ratings ENABLE ROW LEVEL SECURITY;

-- Create policies for user_favorites
CREATE POLICY "Users can view their own favorites"
  ON user_favorites FOR SELECT
  USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own favorites"
  ON user_favorites FOR INSERT
  WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own favorites"
  ON user_favorites FOR UPDATE
  USING (auth.uid() = user_id);

-- Create policies for user_ratings
CREATE POLICY "Anyone can view ratings"
  ON user_ratings FOR SELECT
  USING (true);

CREATE POLICY "Users can insert their own ratings"
  ON user_ratings FOR INSERT
  WITH CHECK (auth.uid() = user_id OR user_id IS NULL);

CREATE POLICY "Users can update their own ratings"
  ON user_ratings FOR UPDATE
  USING (auth.uid() = user_id OR user_id IS NULL);
```

4. Click **Run** (or press Ctrl+Enter)
5. You should see "Success. No rows returned"

## Step 5: Enable Email Authentication

1. Go to **Authentication** → **Providers** in Supabase dashboard
2. Make sure **Email** provider is enabled
3. (Optional) Configure email templates if you want custom emails

## Step 6: Test Your Setup

1. Save your `index.html` file
2. Open the app in your browser
3. Click the Account button (user icon)
4. Try creating an account with a test email
5. Check Supabase dashboard → **Authentication** → **Users** to see if user was created

## Troubleshooting

### "Supabase not configured" error
- Make sure you replaced `YOUR_SUPABASE_URL` and `YOUR_SUPABASE_ANON_KEY` with actual values
- Check that there are no extra spaces or quotes

### "Failed to sync favorites" error
- Check that you created the database tables (Step 4)
- Check browser console for detailed error messages
- Verify RLS policies are created correctly

### Can't create account
- Check Supabase dashboard → Authentication → Providers → Email is enabled
- Check browser console for errors
- Verify your Supabase URL and key are correct

## Security Notes

- The `anon` key is safe to use in frontend code (it's public)
- Row Level Security (RLS) policies protect your data
- Users can only access their own favorites
- Ratings are public (anyone can view) but only logged-in users can rate

## Free Tier Limits

Supabase free tier includes:
- 500 MB database storage
- 2 GB bandwidth
- 50,000 monthly active users
- Unlimited API requests

This should be plenty for most apps!
