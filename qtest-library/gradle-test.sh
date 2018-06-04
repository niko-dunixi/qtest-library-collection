#!/usr/bin/env bash
# Where we store the credentials to interact with qtest
# This should be outside of vcs.
qtest_credentials_file="${HOME}/.qtest-credentials.sh"

# Save credentials if we don't have them.
if [ ! -f "${qtest_credentials_file}" ]; then
    echo -n "Enter your qtest subdomain: "
    read -r subdomain
    echo -n "Enter your qtest username:  "
    read -r username
    echo -n "Enter your qtest password:  "
    read -r -s password
    {
        echo "#!/usr/bin/env bash"
        echo "export QTEST_SUBDOMAIN=\"${subdomain}\""
        echo "export QTEST_USER=\"${username}\""
        echo "export QTEST_PASS=\"${password}\""
    } > "${qtest_credentials_file}"
    chmod 600 "${qtest_credentials_file}"
fi
# Load the credentials in bash
source "${qtest_credentials_file}"
# Run tests and view report
../gradlew test
find . -type f -iname "index.html" -exec open \{\} \;
