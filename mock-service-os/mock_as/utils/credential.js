import Debug from 'debug';
import crypto from "crypto";
const log = Debug('raidiam:server:info');

class Credential {
  constructor(result) {
    this.accountId =  result.accountId;
    this.passwordHash =  result.passwordHash;
    this.passwordSalt =  result.passwordSalt;
    this.failedAuthenticationCount =  result.failedAuthenticationCount;
    this.maxFailedAuthenticationBeforeLockout =  result.maxFailedAuthenticationBeforeLockout;
  }

  static coll(DB) {
    return DB.collection("credentials");
  }

  static async validatePassword(DB, id, password) {
    const result = await Credential.coll(DB).findOne(
      { 'accountId': id.toString() },
    );

    if (!result) {
      log("credential not found in collection")
      return false;
    }

    const hash = crypto.pbkdf2Sync(
        password,         // Input password
        result.passwordSalt,            // Salt
        1000,                // Number of iterations (adjust if different)
        64,                    // Hash length
        'sha512'               // Hash algorithm
    ).toString('hex');
    console.log(hash);
    console.log(result.passwordHash);
    if (!hash || result.passwordHash !== hash) {
      log("passwordHash mismatch");
      return false;
    }

    return true;
  }
}

export default Credential;
