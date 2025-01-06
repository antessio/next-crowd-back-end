#!/usr/bin/env bash
set -e

# Usage: ./newDomain.sh <basePackage> <domainName>
# Example: ./newDomain.sh com.example.myapp Payment

BASE_PACKAGE="$1"
DOMAIN_NAME="$2"
PROJECT_ROOT="./src/main/java/"

if [ -z "$BASE_PACKAGE" ] || [ -z "$DOMAIN_NAME" ]; then
  echo "Usage: $0 <basePackage> <domainName>"
  exit 1
fi

# Convert domain name to lowercase using tr
DOMAIN_NAME_LOWER=$(echo "$DOMAIN_NAME" | tr '[:upper:]' '[:lower:]')

# Replace '.' with '/' in BASE_PACKAGE using sed
BASE_PACKAGE_SLASHED=$(echo "$BASE_PACKAGE" | sed 's/\./\//g')

# Construct the full base path
BASE_PATH="${PROJECT_ROOT}${BASE_PACKAGE_SLASHED}/${DOMAIN_NAME_LOWER}"

# Create the domain folders
mkdir -p "${BASE_PATH}/command"
mkdir -p "${BASE_PATH}/event"
mkdir -p "${BASE_PATH}/exception"
mkdir -p "${BASE_PATH}/model"
mkdir -p "${BASE_PATH}/port"
mkdir -p "${BASE_PATH}/service"

# Create the <DomainName>Service.java file
cat <<EOF > "${BASE_PATH}/${DOMAIN_NAME}Service.java"
package ${BASE_PACKAGE}.${DOMAIN_NAME_LOWER};

public class ${DOMAIN_NAME}Service implements ${DOMAIN_NAME}ServicePort{
    // TODO: Implement service logic
}
EOF

# Create the <DomainName>ServicePort.java file
cat <<EOF > "${BASE_PATH}/${DOMAIN_NAME}ServicePort.java"
package ${BASE_PACKAGE}.${DOMAIN_NAME_LOWER};

public interface ${DOMAIN_NAME}ServicePort {
    // TODO: Define service port methods
}
EOF

echo "Generated domain structure for ${DOMAIN_NAME} in ${BASE_PATH}"
