# Sound Player

To generate a release, add the signing certificate to app/ and execute:

```bash
$ ./gradlew -Palias=<alias> -Ppassword=<password> build
```


To generate obb file:

```bash
$ jobb -d app/src/main/assets/ -o soundplayer.obb -pn com.zauberlabs.soundplayer.app -pv <version_code>
```
