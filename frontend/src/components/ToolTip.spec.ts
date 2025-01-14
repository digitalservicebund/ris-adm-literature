import { render, screen, fireEvent } from '@testing-library/vue'
import { describe, it, expect } from 'vitest'
import ToolTip from '@/components/ToolTip.vue'

describe('ToolTip', () => {
  it('toolTip is hidden by default when isVisible is false', () => {
    render(ToolTip, {
      props: { text: 'This is a toolTip' },
    })

    // ToolTip should not be visible initially
    expect(screen.queryByRole('toolTip')).not.toBeInTheDocument()
  })

  it('opens and closes toolTip on hover and mouseleave', async () => {
    render(ToolTip, {
      props: { text: 'Hovered toolTip' },
      slots: { default: 'Hover over me' },
    })

    const target = screen.getByText('Hover over me')

    // Initially the toolTip should not be visible
    expect(screen.queryByRole('toolTip')).not.toBeInTheDocument()

    // Fire mouseenter event to show the toolTip
    await fireEvent.mouseEnter(target)

    // ToolTip should now be visible
    expect(screen.getByRole('toolTip')).toBeInTheDocument()

    // Fire mouseleave event to hide the toolTip
    await fireEvent.mouseLeave(target)

    // ToolTip should now be hidden
    expect(screen.queryByRole('toolTip')).not.toBeInTheDocument()
  })

  it('opens and closes toolTip on focus and blur', async () => {
    render(ToolTip, {
      props: { text: 'Focused toolTip' },
      slots: { default: 'Hover over me' },
    })

    const target = screen.getByText('Hover over me')

    // Initially the toolTip should not be visible
    expect(screen.queryByRole('toolTip')).not.toBeInTheDocument()

    // Fire focus event to show the toolTip
    await fireEvent.focus(target)

    // ToolTip should now be visible
    expect(screen.getByRole('toolTip')).toBeInTheDocument()

    // Fire blur event to hide the toolTip
    await fireEvent.blur(target)

    // ToolTip should now be hidden
    expect(screen.queryByRole('toolTip')).not.toBeInTheDocument()
  })

  it('closes the toolTip when the Escape key is pressed', async () => {
    render(ToolTip, {
      props: { text: 'Escape toolTip' },
      slots: { default: 'Hover over me' },
    })

    const target = screen.getByText('Hover over me')

    // Initially the toolTip should not be visible
    expect(screen.queryByRole('toolTip')).not.toBeInTheDocument()

    // Fire focus event to show the toolTip
    await fireEvent.focus(target)

    // ToolTip should now be visible
    expect(screen.getByRole('toolTip')).toBeInTheDocument()

    // Fire Escape key event to hide the toolTip
    await fireEvent.keyDown(window, { key: 'Escape' })

    // ToolTip should now be hidden
    expect(screen.queryByRole('toolTip')).not.toBeInTheDocument()
  })

  it('renders the shortcut text when provided', async () => {
    render(ToolTip, {
      props: {
        text: 'ToolTip with shortcut',
        shortcut: 'Ctrl+C',
      },
      slots: { default: 'Hover over me' },
    })

    const target = screen.getByText('Hover over me')

    // Initially the toolTip should not be visible
    expect(screen.queryByRole('toolTip')).not.toBeInTheDocument()

    // Fire focus event to show the toolTip
    await fireEvent.focus(target)

    // ToolTip should now be visible
    expect(screen.getByRole('toolTip')).toBeInTheDocument()

    // Check both the toolTip text and shortcut
    expect(screen.getByRole('toolTip')).toHaveTextContent('ToolTip with shortcut')
    expect(screen.getByRole('toolTip')).toHaveTextContent('Ctrl+C')
  })
})
