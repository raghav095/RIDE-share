# Security Setup Instructions

## Immediate Actions Required

### 1. Rotate Your MongoDB Credentials
Your current MongoDB credentials are compromised. **Immediately**:
1. Go to [Database Access](https://cloud.mongodb.com/v2/6912f2050e423c3bf3af66ef#/security/database)
2. Delete the user `rathiraghavv_db_user` or change its password
3. Create a new database user with a strong password

### 2. Rotate Your JWT Secret
Generate a new secure JWT secret key (minimum 256 bits):
```bash
# On Windows PowerShell, generate a random secret:
-join ((65..90) + (97..122) + (48..57) | Get-Random -Count 64 | ForEach-Object {[char]$_})
```

### 3. Set Environment Variables

#### For Development (Windows)

**Option A: Using System Environment Variables**
1. Open System Properties → Environment Variables
2. Add these variables:
   - `MONGODB_URI` = your new MongoDB connection string
   - `MONGODB_DATABASE` = springbootapplication
   - `JWT_SECRET` = your generated secret key
   - Restart your IDE/terminal

**Option B: Using a .env file with spring-boot-dotenv**
1. Add dependency to `pom.xml`:
```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>
```
2. Create `.env` file in project root (already in .gitignore)
3. Copy `.env.example` to `.env` and fill in actual values

**Option C: IntelliJ IDEA Run Configuration**
1. Edit Run/Debug Configuration
2. Add Environment Variables:
   ```
   MONGODB_URI=mongodb+srv://...;JWT_SECRET=...;MONGODB_DATABASE=springbootapplication
   ```

**Option D: Command Line**
```powershell
$env:MONGODB_URI="mongodb+srv://..."
$env:JWT_SECRET="your-secret-key"
$env:MONGODB_DATABASE="springbootapplication"
mvnw spring-boot:run
```

#### For Production (Cloud Deployment)

**Heroku:**
```bash
heroku config:set MONGODB_URI="mongodb+srv://..."
heroku config:set JWT_SECRET="your-secret-key"
```

**AWS Elastic Beanstalk:**
Add environment properties in the EB Console or `.ebextensions/` config

**Docker:**
```bash
docker run -e MONGODB_URI="..." -e JWT_SECRET="..." your-app
```

**Kubernetes:**
Create a Secret:
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: rideshare-secrets
type: Opaque
stringData:
  MONGODB_URI: "mongodb+srv://..."
  JWT_SECRET: "your-secret-key"
```

### 4. Remove Credentials from Git History

Your credentials are in Git history. You need to:
```bash
# Remove the file from history
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch src/main/resources/application.properties" \
  --prune-empty --tag-name-filter cat -- --all

# Force push (WARNING: coordinate with team first)
git push origin --force --all
```

**Better option:** Use [BFG Repo-Cleaner](https://rtyley.github.io/bfg-repo-cleaner/):
```bash
java -jar bfg.jar --delete-files application.properties
git reflog expire --expire=now --all && git gc --prune=now --aggressive
git push --force
```

### 5. Additional Security Best Practices

1. **Restrict Network Access:**
   - Add IP whitelist in MongoDB Atlas
   - Use Private Endpoints or VPC Peering

2. **Enable Database Auditing:**
   - Monitor access patterns
   - Set up alerts for suspicious activity

3. **Use Strong Authentication:**
   - Enable MFA on MongoDB Atlas account
   - Use AWS IAM or Workload Identity Federation if possible

4. **Monitor Activity:**
   - Check [Activity Feed](https://cloud.mongodb.com/v2/6912f2050e423c3bf3af66ef#/activity)
   - Review [Access Tracking](https://www.mongodb.com/docs/atlas/access-tracking/)

### 6. Test Your Configuration

After setting environment variables:
```bash
mvnw clean spring-boot:run
```

Check the logs to ensure the application connects successfully.

## Verification Checklist

- [ ] Old MongoDB user deleted or password changed
- [ ] New MongoDB user created with strong password
- [ ] New JWT secret generated
- [ ] Environment variables set in your development environment
- [ ] `.env` file created and NOT committed to Git
- [ ] `.gitignore` properly configured
- [ ] Application starts successfully with new credentials
- [ ] Old credentials removed from Git history
- [ ] Force push completed to GitHub
- [ ] MongoDB Atlas IP whitelist configured
- [ ] Database auditing enabled (if required)
- [ ] Activity feed checked for suspicious activity

## Support

If you need help, contact MongoDB support at https://support.mongodb.com/
