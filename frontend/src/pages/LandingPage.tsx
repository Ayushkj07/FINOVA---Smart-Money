import React from 'react';
import { Link } from 'react-router-dom';
import {
  Sparkles,
  ArrowRight,
  ShieldCheck,
  PiggyBank,
  PieChart,
  Repeat,
  HandCoins,
  CheckCircle2,
  TrendingUp,
  BrainCircuit,
  Receipt,
  Users2,
  HelpCircle,
  ChevronDown
} from 'lucide-react';

export const LandingPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-white text-finova-text selection:bg-lavender-100 selection:text-primary">
      {/* 1. HEADER */}
      <header className="sticky top-0 z-50 bg-white/95 backdrop-blur-sm border-b border-finova-border">
        <div className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-2xl bg-primary flex items-center justify-center shadow-sm">
              <span className="text-white font-extrabold text-xl tracking-wider">F</span>
            </div>
            <div>
              <span className="font-extrabold text-xl tracking-tight text-finova-text">FINOVA</span>
              <span className="block text-[10px] text-finova-muted font-medium -mt-1">Smart Money. Smarter Future.</span>
            </div>
          </div>

          <nav className="hidden md:flex items-center gap-8 text-sm font-semibold text-finova-muted">
            <a href="#features" className="hover:text-primary transition-colors">Features</a>
            <a href="#how-it-works" className="hover:text-primary transition-colors">How It Works</a>
            <a href="#savings" className="hover:text-primary transition-colors">Savings Goals</a>
            <a href="#faq" className="hover:text-primary transition-colors">FAQ</a>
          </nav>

          <div className="flex items-center gap-4">
            <Link
              to="/login"
              className="text-sm font-bold text-finova-text hover:text-primary px-4 py-2.5 transition-colors"
            >
              Sign In
            </Link>
            <Link
              to="/register"
              className="inline-flex items-center gap-2 bg-primary hover:bg-primary-dark text-white text-sm font-bold px-5 py-2.5 rounded-xl transition-all shadow-sm shadow-primary/20 hover:scale-[1.02]"
            >
              Get Started Free
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        </div>
      </header>

      {/* 2. HERO SECTION */}
      <section className="relative pt-16 pb-24 overflow-hidden bg-gradient-to-b from-white via-secondary-bg/50 to-white">
        <div className="max-w-6xl mx-auto px-6 text-center">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-lavender-100 text-primary-dark text-xs font-bold mb-6 border border-lavender-200">
            <Sparkles className="w-3.5 h-3.5 text-primary" />
            <span>AI-Powered Personal Finance for Students</span>
          </div>

          <h1 className="text-4xl sm:text-6xl font-extrabold text-finova-text tracking-tight leading-[1.15] max-w-4xl mx-auto mb-6">
            Take Control of Your Money. <br />
            <span className="text-primary">Build Your Future.</span>
          </h1>

          <p className="text-lg sm:text-xl text-finova-muted max-w-2xl mx-auto font-normal leading-relaxed mb-10">
            Your personal financial companion to track spending, manage pocket money, prevent overspending, and grow savings with Google Gemini AI.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-16">
            <Link
              to="/register"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 bg-primary hover:bg-primary-dark text-white text-base font-bold px-8 py-3.5 rounded-2xl shadow-md shadow-primary/25 hover:scale-[1.02] transition-all"
            >
              Get Started Free
              <ArrowRight className="w-5 h-5" />
            </Link>
            <a
              href="#features"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 bg-white hover:bg-secondary-bg text-finova-text text-base font-bold px-8 py-3.5 rounded-2xl border border-finova-border transition-all"
            >
              Explore Features
            </a>
          </div>

          {/* Dashboard Preview Graphic */}
          <div className="relative mx-auto max-w-5xl rounded-3xl p-3 bg-gradient-to-b from-lavender-200/40 via-white/80 to-transparent border border-finova-border/80 shadow-2xl shadow-primary/10">
            <div className="rounded-2xl bg-white border border-finova-border p-6 sm:p-8 text-left">
              <div className="flex items-center justify-between pb-6 border-b border-finova-border">
                <div className="flex items-center gap-3">
                  <div className="w-3 h-3 rounded-full bg-red-400" />
                  <div className="w-3 h-3 rounded-full bg-yellow-400" />
                  <div className="w-3 h-3 rounded-full bg-green-400" />
                  <span className="text-xs text-finova-muted font-mono ml-2">app.finova.finance/dashboard</span>
                </div>
                <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-sage-100 text-sage-800">
                  Health Score: 88 (Excellent)
                </span>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-4 gap-4 mt-6">
                <div className="p-4 rounded-xl bg-secondary-bg border border-finova-border">
                  <span className="text-xs text-finova-muted font-medium">Total Balance</span>
                  <p className="text-2xl font-black text-finova-text mt-1">₹14,750.00</p>
                  <span className="text-[11px] font-bold text-finova-success flex items-center gap-1 mt-1">
                    <TrendingUp className="w-3 h-3" /> +12.4% this month
                  </span>
                </div>
                <div className="p-4 rounded-xl bg-secondary-bg border border-finova-border">
                  <span className="text-xs text-finova-muted font-medium">Monthly Inflow</span>
                  <p className="text-2xl font-black text-finova-text mt-1">₹25,000.00</p>
                  <span className="text-[11px] text-finova-muted mt-1 block">Allowance & Stipends</span>
                </div>
                <div className="p-4 rounded-xl bg-secondary-bg border border-finova-border">
                  <span className="text-xs text-finova-muted font-medium">Total Expenses</span>
                  <p className="text-2xl font-black text-finova-text mt-1">₹10,250.00</p>
                  <span className="text-[11px] font-bold text-sage-700 mt-1 block">Within 75% Budget Limit</span>
                </div>
                <div className="p-4 rounded-xl bg-secondary-bg border border-finova-border">
                  <span className="text-xs text-finova-muted font-medium">Active Savings</span>
                  <p className="text-2xl font-black text-primary mt-1">₹4,500.00</p>
                  <span className="text-[11px] text-finova-muted mt-1 block">MacBook M3 Vault (68%)</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 3. WHY FINOVA? */}
      <section className="py-20 bg-secondary-bg border-y border-finova-border">
        <div className="max-w-6xl mx-auto px-6">
          <div className="text-center max-w-2xl mx-auto mb-16">
            <span className="text-xs font-bold uppercase tracking-wider text-primary">Why FINOVA?</span>
            <h2 className="text-3xl sm:text-4xl font-extrabold text-finova-text tracking-tight mt-2">
              Built Specifically for the College Journey
            </h2>
            <p className="text-finova-muted mt-3 text-base">
              Traditional finance tools assume mortgages and corporate 401(k)s. FINOVA is designed around student allowances, campus canteens, shared trip expenses, and student subscriptions.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="p-8 rounded-2xl bg-white border border-finova-border shadow-sm hover:shadow-md transition-shadow">
              <div className="w-12 h-12 rounded-xl bg-lavender-100 flex items-center justify-center text-primary mb-6">
                <BrainCircuit className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-finova-text mb-2">Student AI Financial Coach</h3>
              <p className="text-sm text-finova-muted leading-relaxed">
                Powered by Google Gemini, FINOVA reviews your real transactions and spots where you leak pocket money, without judgmental jargon.
              </p>
            </div>

            <div className="p-8 rounded-2xl bg-white border border-finova-border shadow-sm hover:shadow-md transition-shadow">
              <div className="w-12 h-12 rounded-xl bg-sage-100 flex items-center justify-center text-sage-700 mb-6">
                <PieChart className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-finova-text mb-2">3-Tier Smart Alerts</h3>
              <p className="text-sm text-finova-muted leading-relaxed">
                Stay disciplined with automated 70% caution, 90% high warning, and 100% threshold limits across campus food, travel, and lifestyle.
              </p>
            </div>

            <div className="p-8 rounded-2xl bg-white border border-finova-border shadow-sm hover:shadow-md transition-shadow">
              <div className="w-12 h-12 rounded-xl bg-yellow-50 flex items-center justify-center text-finova-warning mb-6">
                <HandCoins className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-finova-text mb-2">Peer Debts & Split Bills</h3>
              <p className="text-sm text-finova-muted leading-relaxed">
                Never lose track of money borrowed or lent to friends. Record partial repayments, send friendly reminders, and keep relationships stress-free.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* 4. KEY FEATURES GRID */}
      <section id="features" className="py-24 bg-white">
        <div className="max-w-6xl mx-auto px-6">
          <div className="text-center max-w-2xl mx-auto mb-16">
            <span className="text-xs font-bold uppercase tracking-wider text-primary">All-in-One Fintech Suite</span>
            <h2 className="text-3xl sm:text-4xl font-extrabold text-finova-text tracking-tight mt-2">
              Everything You Need to Master Your Money
            </h2>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[
              {
                icon: <PieChart className="w-5 h-5 text-primary" />,
                title: 'Smart Budget Planner',
                desc: 'Set category-wise caps and track real-time utilization with 70%, 90%, and 100% warnings.',
              },
              {
                icon: <Receipt className="w-5 h-5 text-sage-700" />,
                title: 'Instant Receipt Scanner',
                desc: 'Upload cafeteria and grocery receipts; intelligent parsing extracts amounts, dates, and vendors automatically.',
              },
              {
                icon: <PiggyBank className="w-5 h-5 text-primary" />,
                title: 'Target Savings Vaults',
                desc: 'Save for laptops, semester exams, or dream getaways with automated progress trackers and completion dates.',
              },
              {
                icon: <Repeat className="w-5 h-5 text-finova-warning" />,
                title: 'Subscription Radar',
                desc: 'Track Spotify, Netflix, and software renewals. Catch forgotten trials before they deduct your balance.',
              },
              {
                icon: <Users2 className="w-5 h-5 text-primary" />,
                title: 'Roommate Bill Splitter',
                desc: 'Split flats, WiFi bills, and group dining with friends. Track who paid and who owes with zero friction.',
              },
              {
                icon: <ShieldCheck className="w-5 h-5 text-finova-success" />,
                title: 'Financial Health Score',
                desc: 'Get an objective 0–100 score evaluating savings consistency, debt ratio, and budget discipline with actionable advice.',
              },
            ].map((f, i) => (
              <div
                key={i}
                className="p-6 rounded-2xl border border-finova-border bg-white hover:border-lavender-200 hover:shadow-md transition-all"
              >
                <div className="w-10 h-10 rounded-xl bg-secondary-bg flex items-center justify-center mb-4 border border-finova-border">
                  {f.icon}
                </div>
                <h4 className="text-base font-bold text-finova-text mb-1.5">{f.title}</h4>
                <p className="text-xs text-finova-muted leading-relaxed">{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 5. HOW IT WORKS */}
      <section id="how-it-works" className="py-20 bg-secondary-bg border-y border-finova-border">
        <div className="max-w-6xl mx-auto px-6">
          <div className="text-center max-w-2xl mx-auto mb-16">
            <span className="text-xs font-bold uppercase tracking-wider text-primary">Effortless Workflow</span>
            <h2 className="text-3xl sm:text-4xl font-extrabold text-finova-text tracking-tight mt-2">
              Three Simple Steps to Financial Freedom
            </h2>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 text-center">
            <div className="p-6">
              <div className="w-14 h-14 rounded-2xl bg-lavender-100 text-primary font-black text-xl flex items-center justify-center mx-auto mb-5 border border-lavender-200">
                1
              </div>
              <h3 className="text-lg font-bold text-finova-text mb-2">Log Inflows & Outflows</h3>
              <p className="text-sm text-finova-muted">
                Record your monthly pocket money, freelancing gigs, UPI payments, and cash in seconds.
              </p>
            </div>

            <div className="p-6">
              <div className="w-14 h-14 rounded-2xl bg-sage-100 text-sage-800 font-black text-xl flex items-center justify-center mx-auto mb-5 border border-sage-200">
                2
              </div>
              <h3 className="text-lg font-bold text-finova-text mb-2">Set Budgets & Vaults</h3>
              <p className="text-sm text-finova-muted">
                Assign monthly spending limits and define savings milestones with automated progress tracking.
              </p>
            </div>

            <div className="p-6">
              <div className="w-14 h-14 rounded-2xl bg-lavender-100 text-primary font-black text-xl flex items-center justify-center mx-auto mb-5 border border-lavender-200">
                3
              </div>
              <h3 className="text-lg font-bold text-finova-text mb-2">Get AI Coaching & Insights</h3>
              <p className="text-sm text-finova-muted">
                Receive personalized AI advice, weekly spending reviews, and maintain an excellent Financial Health Score.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* 6. TESTIMONIALS (IDENTIFIED DEMO CONTENT) */}
      <section className="py-24 bg-white">
        <div className="max-w-6xl mx-auto px-6">
          <div className="text-center max-w-2xl mx-auto mb-16">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-secondary-bg border border-finova-border text-[11px] font-bold text-finova-muted mb-3">
              <span>Demo Student Perspectives</span>
            </div>
            <h2 className="text-3xl sm:text-4xl font-extrabold text-finova-text tracking-tight">
              Trusted by Ambitious Students Everywhere
            </h2>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {[
              {
                name: 'Alex Morgan',
                college: 'Stanford University (Demo User)',
                quote: 'FINOVA completely changed how I look at my monthly stipend. In three months, I saved enough for my MacBook upgrade without skipping weekend hangouts.',
                tag: 'Saved ₹42,000 in 3 Months',
              },
              {
                name: 'Priya Sharma',
                college: 'IIT Delhi (Demo User)',
                quote: 'Tracking cafeteria UPI spends and flat rent splits used to be a mess. FINOVA AI gave me realistic weekly limits that actually work for student life.',
                tag: 'Debt Free & 88 Health Score',
              },
              {
                name: 'David Chen',
                college: 'University of Toronto (Demo User)',
                quote: 'The 70% and 90% budget alert levels saved me from going broke before the end of exam month. Clean, minimalist, and no annoying dark mode or bloat.',
                tag: 'Discipline Master',
              },
            ].map((t, i) => (
              <div key={i} className="p-8 rounded-2xl border border-finova-border bg-secondary-bg/30 flex flex-col justify-between">
                <div>
                  <div className="inline-block px-2.5 py-1 rounded-md bg-sage-100 text-sage-800 text-[11px] font-bold mb-4">
                    {t.tag}
                  </div>
                  <p className="text-sm text-finova-text font-medium leading-relaxed italic mb-6">
                    "{t.quote}"
                  </p>
                </div>
                <div className="pt-4 border-t border-finova-border flex items-center gap-3">
                  <div className="w-9 h-9 rounded-full bg-lavender-100 text-primary font-bold flex items-center justify-center text-xs">
                    {t.name[0]}
                  </div>
                  <div>
                    <h5 className="text-xs font-bold text-finova-text">{t.name}</h5>
                    <p className="text-[11px] text-finova-muted">{t.college}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 7. FAQ */}
      <section id="faq" className="py-20 bg-secondary-bg border-y border-finova-border">
        <div className="max-w-4xl mx-auto px-6">
          <div className="text-center mb-16">
            <span className="text-xs font-bold uppercase tracking-wider text-primary">Got Questions?</span>
            <h2 className="text-3xl font-extrabold text-finova-text tracking-tight mt-2">
              Frequently Asked Questions
            </h2>
          </div>

          <div className="space-y-4">
            {[
              {
                q: 'Is FINOVA free for students?',
                a: 'Yes! FINOVA is completely free for college students to track expenses, set budgets, and use the AI financial assistant.',
              },
              {
                q: 'How does FINOVA AI help me manage money?',
                a: 'FINOVA AI utilizes Google Gemini to analyze your actual income, expenses, and savings habits. It highlights recurring anomalies and provides actionable budgeting tips tailored for students.',
              },
              {
                q: 'Can I track both Cash and UPI payments?',
                a: 'Yes. Every transaction supports Cash, UPI, Debit Card, Credit Card, and Bank Transfer tags for accurate breakdown analysis.',
              },
              {
                q: 'Is my financial data secure?',
                a: 'Absolutely. We enforce industry-standard BCrypt password hashing, stateless JWT authentication with refresh token rotation, and strict tenant isolation so students only access their own records.',
              },
            ].map((faq, i) => (
              <div key={i} className="p-6 rounded-2xl bg-white border border-finova-border">
                <h4 className="text-base font-bold text-finova-text flex items-center gap-2">
                  <HelpCircle className="w-4 h-4 text-primary flex-shrink-0" />
                  {faq.q}
                </h4>
                <p className="text-xs text-finova-muted leading-relaxed mt-2 pl-6">
                  {faq.a}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 8. CTA SECTION */}
      <section className="py-24 bg-white text-center">
        <div className="max-w-4xl mx-auto px-6">
          <div className="p-12 rounded-3xl bg-lavender-50 border border-lavender-200">
            <h2 className="text-3xl sm:text-4xl font-extrabold text-finova-text tracking-tight">
              Ready to Upgrade Your Financial Future?
            </h2>
            <p className="text-finova-muted mt-3 max-w-xl mx-auto text-base">
              Join thousands of students mastering their finances today with FINOVA. Zero setup fees, instant access.
            </p>
            <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-4">
              <Link
                to="/register"
                className="inline-flex items-center gap-2 bg-primary hover:bg-primary-dark text-white font-bold px-8 py-3.5 rounded-2xl shadow-md shadow-primary/20 hover:scale-[1.02] transition-all"
              >
                Create Free Account
                <ArrowRight className="w-4 h-4" />
              </Link>
              <Link
                to="/login"
                className="inline-flex items-center gap-2 bg-white hover:bg-secondary-bg text-finova-text font-bold px-8 py-3.5 rounded-2xl border border-finova-border transition-all"
              >
                Log In to Existing Account
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* 9. FOOTER */}
      <footer className="bg-white border-t border-finova-border py-12">
        <div className="max-w-7xl mx-auto px-6 flex flex-col sm:flex-row items-center justify-between gap-6">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-xl bg-primary flex items-center justify-center">
              <span className="text-white font-black text-sm">F</span>
            </div>
            <span className="font-extrabold text-base text-finova-text">FINOVA</span>
            <span className="text-xs text-finova-muted">© 2026 FINOVA Platform. All rights reserved.</span>
          </div>

          <div className="flex items-center gap-6 text-xs text-finova-muted font-medium">
            <span>Educational Fintech Platform</span>
            <span>Privacy Policy</span>
            <span>Terms of Service</span>
            <span>Security</span>
          </div>
        </div>
      </footer>
    </div>
  );
};
