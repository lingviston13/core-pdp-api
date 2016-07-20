[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2804cd619dde437a883da48ad5c283bc)](https://www.codacy.com/app/coder103/authzforce-ce-core-pdp-api?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=authzforce/core-pdp-api&amp;utm_campaign=Badge_Grade)

# AuthZForce Core PDP API
High-level API for using AuthZForce PDP engine and implementing PDP engine extensions: attribute datatypes, functions, policy/rule combining algorithms, attribute providers, policy providers, XACML Request/Result filters, etc.

## Contributing
### Contribution Rules
1. No SNAPSHOT dependencies on "develop" and obviously "master" branches

### Releasing
1. From the develop branch, prepare a release (example using a HTTP proxy):
<pre><code>
    $ mvn -Dhttps.proxyHost=proxyhostname -Dhttps.proxyPort=3128 jgitflow:release-start
</code></pre>
1. Update the CHANGELOG according to keepachangelog.com.
1. To perform the release (example using a HTTP proxy):
<pre><code>
    $ mvn -Dhttps.proxyHost=proxyhostname -Dhttps.proxyPort=3128 jgitflow:release-finish
</code></pre>
    If, after deployment, the command does not succeed because of some issue with the branches. Fix the issue, then re-run the same command but with 'noDeploy' option set to true to avoid re-deployment:
<pre><code>
    $ mvn -Dhttps.proxyHost=proxyhostname -Dhttps.proxyPort=3128 -DnoDeploy=true jgitflow:release-finish
</code></pre>
1. Connect and log in to the OSS Nexus Repository Manager: https://oss.sonatype.org/
1. Go to Staging Profiles and select the pending repository authzforce-*... you just uploaded with `jgitflow:release-finish`
1. Click the Release button to release to Maven Central.

More info on jgitflow: http://jgitflow.bitbucket.org/
