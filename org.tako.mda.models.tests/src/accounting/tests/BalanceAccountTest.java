/**
 */
package accounting.tests;

import accounting.AccountingFactory;
import accounting.BalanceAccount;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Balance Account</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class BalanceAccountTest extends AccountTest {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(BalanceAccountTest.class);
	}

	/**
	 * Constructs a new Balance Account test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BalanceAccountTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Balance Account test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected BalanceAccount getFixture() {
		return (BalanceAccount)fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(AccountingFactory.eINSTANCE.createBalanceAccount());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

} //BalanceAccountTest
