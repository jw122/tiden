# Tiden

## getting started
`source .env`

## deployment
### frontend
from the root folder
```bash
yarn --cwd ./frontend/ build && aws s3 sync ./frontend/build/ $S3_BUCKET_HOSTED_SITE
```

### for the backend, see ./server/README.md