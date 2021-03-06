/**
 */
package accounting.provider;

import accounting.util.AccountingAdapterFactory;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class AccountingItemProviderAdapterFactory extends AccountingAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier, IDisposable {
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This constructs an instance.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AccountingItemProviderAdapterFactory() {
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This keeps track of the one adapter used for all {@link accounting.AccountGroup} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AccountGroupItemProvider accountGroupItemProvider;

	/**
	 * This creates an adapter for a {@link accounting.AccountGroup}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createAccountGroupAdapter() {
		if (accountGroupItemProvider == null) {
			accountGroupItemProvider = new AccountGroupItemProvider(this);
		}

		return accountGroupItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link accounting.Accounting} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AccountingItemProvider accountingItemProvider;

	/**
	 * This creates an adapter for a {@link accounting.Accounting}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createAccountingAdapter() {
		if (accountingItemProvider == null) {
			accountingItemProvider = new AccountingItemProvider(this);
		}

		return accountingItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link accounting.BalanceAccount} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BalanceAccountItemProvider balanceAccountItemProvider;

	/**
	 * This creates an adapter for a {@link accounting.BalanceAccount}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createBalanceAccountAdapter() {
		if (balanceAccountItemProvider == null) {
			balanceAccountItemProvider = new BalanceAccountItemProvider(this);
		}

		return balanceAccountItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link accounting.JournalGroup} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected JournalGroupItemProvider journalGroupItemProvider;

	/**
	 * This creates an adapter for a {@link accounting.JournalGroup}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createJournalGroupAdapter() {
		if (journalGroupItemProvider == null) {
			journalGroupItemProvider = new JournalGroupItemProvider(this);
		}

		return journalGroupItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link accounting.JournalStatement} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected JournalStatementItemProvider journalStatementItemProvider;

	/**
	 * This creates an adapter for a {@link accounting.JournalStatement}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createJournalStatementAdapter() {
		if (journalStatementItemProvider == null) {
			journalStatementItemProvider = new JournalStatementItemProvider(this);
		}

		return journalStatementItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link accounting.PLAccount} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PLAccountItemProvider plAccountItemProvider;

	/**
	 * This creates an adapter for a {@link accounting.PLAccount}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createPLAccountAdapter() {
		if (plAccountItemProvider == null) {
			plAccountItemProvider = new PLAccountItemProvider(this);
		}

		return plAccountItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link accounting.Report} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReportItemProvider reportItemProvider;

	/**
	 * This creates an adapter for a {@link accounting.Report}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createReportAdapter() {
		if (reportItemProvider == null) {
			reportItemProvider = new ReportItemProvider(this);
		}

		return reportItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link accounting.ReportGroup} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReportGroupItemProvider reportGroupItemProvider;

	/**
	 * This creates an adapter for a {@link accounting.ReportGroup}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createReportGroupAdapter() {
		if (reportGroupItemProvider == null) {
			reportGroupItemProvider = new ReportGroupItemProvider(this);
		}

		return reportGroupItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link accounting.Vat} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected VatItemProvider vatItemProvider;

	/**
	 * This creates an adapter for a {@link accounting.Vat}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createVatAdapter() {
		if (vatItemProvider == null) {
			vatItemProvider = new VatItemProvider(this);
		}

		return vatItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class<?>) || (((Class<?>)type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * This disposes all of the item providers created by this factory. 
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void dispose() {
		if (accountGroupItemProvider != null) accountGroupItemProvider.dispose();
		if (accountingItemProvider != null) accountingItemProvider.dispose();
		if (balanceAccountItemProvider != null) balanceAccountItemProvider.dispose();
		if (journalGroupItemProvider != null) journalGroupItemProvider.dispose();
		if (journalStatementItemProvider != null) journalStatementItemProvider.dispose();
		if (plAccountItemProvider != null) plAccountItemProvider.dispose();
		if (reportItemProvider != null) reportItemProvider.dispose();
		if (reportGroupItemProvider != null) reportGroupItemProvider.dispose();
		if (vatItemProvider != null) vatItemProvider.dispose();
	}

}
