SUMMARY = "Command line tool and library for client-side URL transfers"
DESCRIPTION = "It uses URL syntax to transfer data to and from servers. \
curl is a widely used because of its ability to be flexible and complete \
complex tasks. For example, you can use curl for things like user authentication, \
HTTP post, SSL connections, proxy support, FTP uploads, and more!"
HOMEPAGE = "http://curl.haxx.se/"
BUGTRACKER = "http://curl.haxx.se/mail/list.cgi?list=curl-tracker"
SECTION = "console/network"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=190c514872597083303371684954f238"

SRC_URI = "https://curl.haxx.se/download/curl-${PV}.tar.bz2 \
           file://0001-replace-krb5-config-with-pkg-config.patch \
           file://CVE-2022-32205-cookie-apply-limits.patch \
           file://CVE-2022-32206-return-error-on-too-many-compression-steps.patch \
           file://CVE-2022-32207-fopen-add-Curl_fopen-for-better-overwriting-of-fi.patch \
           file://CVE-2022-32208-krb5-return-error-properly-on-decode-errors.patch \
"

SRC_URI[sha256sum] = "f539a36fb44a8260ec5d977e4e0dbdd2eee29ed90fcedaa9bc3c9f78a113bff0"

# Curl has used many names over the years...
CVE_PRODUCT = "haxx:curl haxx:libcurl curl:curl curl:libcurl libcurl:libcurl daniel_stenberg:curl"

inherit autotools pkgconfig binconfig multilib_header

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} ssl libidn proxy threaded-resolver verbose zlib"
PACKAGECONFIG_class-native = "ipv6 proxy ssl threaded-resolver verbose zlib"
PACKAGECONFIG_class-nativesdk = "ipv6 proxy ssl threaded-resolver verbose zlib"

# 'ares' and 'threaded-resolver' are mutually exclusive
PACKAGECONFIG[ares] = "--enable-ares,--disable-ares,c-ares,,,threaded-resolver"
PACKAGECONFIG[brotli] = "--with-brotli,--without-brotli,brotli"
PACKAGECONFIG[builtinmanual] = "--enable-manual,--disable-manual"
PACKAGECONFIG[dict] = "--enable-dict,--disable-dict,"
PACKAGECONFIG[gnutls] = "--with-gnutls,--without-gnutls,gnutls"
PACKAGECONFIG[gopher] = "--enable-gopher,--disable-gopher,"
PACKAGECONFIG[imap] = "--enable-imap,--disable-imap,"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[krb5] = "--with-gssapi,--without-gssapi,krb5"
PACKAGECONFIG[ldap] = "--enable-ldap,--disable-ldap,"
PACKAGECONFIG[ldaps] = "--enable-ldaps,--disable-ldaps,"
PACKAGECONFIG[libgsasl] = "--with-libgsasl,--without-libgsasl,libgsasl"
PACKAGECONFIG[libidn] = "--with-libidn2,--without-libidn2,libidn2"
PACKAGECONFIG[libssh2] = "--with-libssh2,--without-libssh2,libssh2"
PACKAGECONFIG[mbedtls] = "--with-mbedtls=${STAGING_DIR_TARGET},--without-mbedtls,mbedtls"
PACKAGECONFIG[mqtt] = "--enable-mqtt,--disable-mqtt,"
PACKAGECONFIG[nghttp2] = "--with-nghttp2,--without-nghttp2,nghttp2"
PACKAGECONFIG[pop3] = "--enable-pop3,--disable-pop3,"
PACKAGECONFIG[proxy] = "--enable-proxy,--disable-proxy,"
PACKAGECONFIG[rtmpdump] = "--with-librtmp,--without-librtmp,rtmpdump"
PACKAGECONFIG[rtsp] = "--enable-rtsp,--disable-rtsp,"
PACKAGECONFIG[smb] = "--enable-smb,--disable-smb,"
PACKAGECONFIG[smtp] = "--enable-smtp,--disable-smtp,"
PACKAGECONFIG[ssl] = "--with-ssl --with-random=/dev/urandom,--without-ssl,openssl"
PACKAGECONFIG[nss] = "--with-nss,--without-nss,nss"
PACKAGECONFIG[telnet] = "--enable-telnet,--disable-telnet,"
PACKAGECONFIG[tftp] = "--enable-tftp,--disable-tftp,"
PACKAGECONFIG[threaded-resolver] = "--enable-threaded-resolver,--disable-threaded-resolver,,,,ares"
PACKAGECONFIG[verbose] = "--enable-verbose,--disable-verbose"
PACKAGECONFIG[zlib] = "--with-zlib=${STAGING_LIBDIR}/../,--without-zlib,zlib"

EXTRA_OECONF = " \
    --disable-libcurl-option \
    --disable-ntlm-wb \
    --enable-crypto-auth \
    --with-ca-bundle=${sysconfdir}/ssl/certs/ca-certificates.crt \
    --without-libpsl \
    --enable-debug \
    --enable-optimize \
    --disable-curldebug \
"

do_install:append:class-target() {
	# cleanup buildpaths from curl-config
	sed -i \
	    -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's,--with-libtool-sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    ${D}${bindir}/curl-config
}

PACKAGES =+ "lib${BPN}"

FILES_lib${BPN} = "${libdir}/lib*.so.*"
RRECOMMENDS_lib${BPN} += "ca-certificates"

FILES_${PN} += "${datadir}/zsh"

inherit multilib_script
MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/curl-config"

BBCLASSEXTEND = "native nativesdk"
